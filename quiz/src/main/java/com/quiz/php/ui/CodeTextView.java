package com.quiz.php.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fabricio on 2/18/14.
 */
public class CodeTextView extends TextView {

    private static final String TAG = "codetextview";

    private static final int COLOR_ERROR = 0x80ff0000;
    private static final int COLOR_BUILTIN = 0xffd79e39;
    private static final int COLOR_COMMENT = 0xff808080;

    private static final String COLOR_NUMBER = "#6897BB";
    private static final String COLOR_STRING = "#6A8759";
    private static final String COLOR_VARIABLE = "#6F599D";
    private static final String COLOR_KEYWORD = "#CC7832";

    private static final Pattern line = Pattern.compile(
            ".*\\n" );
    private static final Pattern numbers = Pattern.compile(
            "\\b(\\d*[.]?\\d+)\\b" );
    private static final Pattern keywords = Pattern.compile(
            "\\b(__halt_compiler|abstract|and|array|as|break|callable|case|catch|class|clone|const|continue|declare|default|die|do|echo|else|elseif|empty|enddeclare|endfor|endforeach|endif|endswitch|endwhile|eval|exit|extends|final|for|foreach|function|global|goto|if|implements|include|include_once|instanceof|insteadof|interface|isset|list|namespace|new|or|print|private|protected|public|require|require_once|return|static|switch|throw|trait|try|unset|use|var|while|xor)\\b" );

    private static final Pattern builtins = Pattern.compile(
            "\\b(strlen|array_merge|count)\\b" );

    private static final Pattern variables = Pattern.compile(
            "\\$[a-zA-Z_0-9]+" );

    private static final Pattern comments = Pattern.compile(
            "/\\*(?:.|[\\n\\r])*?\\*/|//.*" );

    private static final Pattern strings = Pattern.compile(
            "(?:\"|')(?:.|[\\n\\r])*?(?:\"|')"  );

    private static final Pattern code = Pattern.compile(
            "\\[code\\](?:.|[\\n\\r])*?\\[\\/code\\]", Pattern.MULTILINE);


    private Handler mHandler = new Handler();
    private Runnable mRunnable =
            new Runnable()
            {
                @Override
                public void run()
                {
                    SpannableStringBuilder string = new SpannableStringBuilder(mText);

                    for (Matcher m = code.matcher( mText ); m.find();) {
                        string.replace(
                                m.start(),
                                m.end(),
                                highlight(new SpannableStringBuilder(m.group().replaceAll("\\[\\/?code\\]", "")))
                        );
                    }

                    setText(string);
                }
            };
    private CharSequence mText;


    public CodeTextView(Context context) {
        super(context);
        init();
    }

    public CodeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public void setTextHighlighted( CharSequence text )
    {
         mText = text;
         mHandler.post(mRunnable);
    }

    private Editable highlight( Editable e )
    {
        try
        {
            e.clearSpans();

            if( e.length() == 0 )
                return e;

            for( Matcher m = numbers.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(Color.parseColor(COLOR_NUMBER)),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = keywords.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(Color.parseColor(COLOR_KEYWORD)),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = variables.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(Color.parseColor(COLOR_VARIABLE)),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = builtins.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( COLOR_BUILTIN ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = strings.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan(Color.parseColor(COLOR_STRING)),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

            for( Matcher m = comments.matcher( e );
                 m.find(); )
                e.setSpan(
                        new ForegroundColorSpan( COLOR_COMMENT ),
                        m.start(),
                        m.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        }
        catch( Exception ex )
        {
        }
//
//        e.setSpan(
//                new BackgroundColorSpan( COLOR_COMMENT ),
//                1, e.length() -1,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );

//        e.setSpan(new TypefaceSpan("monospace"), 0, e.length() -1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
        return e;
    }




    private void init () {

        setHorizontallyScrolling(false);

        setFilters( new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(
                            CharSequence source,
                            int start,
                            int end,
                            Spanned dest,
                            int dstart,
                            int dend )
                    {
                        if( end - start == 1 &&
                                start < source.length() &&
                                dstart < dest.length() ) {

                            char c = source.charAt( start );

                            if( c == '\n' )
                                return autoIndent(
                                        source,
                                        start,
                                        end,
                                        dest,
                                        dstart,
                                        dend );
                        }

                        return source;
                    }
                }
        });

    }



    private CharSequence autoIndent(
            CharSequence source,
            int start,
            int end,
            Spanned dest,
            int dstart,
            int dend )
    {
        String indent = "";
        int istart = dstart-1;
        int iend = -1;

        // find start of this line
        boolean dataBefore = false;
        int pt = 0;

        for( ; istart > -1; --istart )
        {
            char c = dest.charAt( istart );

            if( c == '\n' )
                break;

            if( c != ' ' &&
                    c != '\t' )
            {
                if( !dataBefore )
                {
                    // indent always after those characters
                    if( c == '{' ||
                            c == '+' ||
                            c == '-' ||
                            c == '*' ||
                            c == '/' ||
                            c == '%' ||
                            c == '^' ||
                            c == '=' )
                        --pt;

                    dataBefore = true;
                }

                // parenthesis counter
                if( c == '(' )
                    --pt;
                else if( c == ')' )
                    ++pt;
            }
        }

        // copy indent of this line into the next
        if( istart > -1 )
        {
            char charAtCursor = dest.charAt( dstart );

            for( iend = ++istart;
                 iend < dend;
                 ++iend )
            {
                char c = dest.charAt( iend );

                // auto expand comments
                if( charAtCursor != '\n' &&
                        c == '/' &&
                        iend+1 < dend &&
                        dest.charAt( iend ) == c )
                {
                    iend += 2;
                    break;
                }

                if( c != ' ' &&
                        c != '\t' )
                    break;
            }

            indent += dest.subSequence( istart, iend );
        }

        // add new indent
        if( pt < 0 )
            indent += "\t";

        // append white space of previous line and new indent
        return source+indent;
    }

}
