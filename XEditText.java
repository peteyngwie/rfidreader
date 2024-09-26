package com.smartcity.cgs;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;


/**
 * 按照自己想要的格式自動分割顯示的 EditText 默認手機號碼格式：xxx xxxx xxxx
 * 也可自任意格式，如信用卡格式：xxxx-xxxx-xxxx-xxxx 或 xxxx xxxx xxxx xxxx
 * 使用 pattern时无需在xml中设定 maxLength，若需要設定时要注意加上分隔符的数量
 * com.z.customedittext.XEditText
 * ContentSeparatorEditText
 */
public class XEditText extends androidx.appcompat.widget.AppCompatEditText {



    private static final String SPACE = " ";
    private static final int[] DEFAULT_PATTERN = new int[] { 4,10 };

    private OnTextChangeListener mTextChangeListener;
    private TextWatcher mTextWatcher;

    private int preLength;
    private int currLength;

    private int[] pattern; // 模板
    private int[] intervals; // 根据模板控制分隔符的插入位置
    private String separator; // 分割符，默认使用空格分割
    // 根据模板自动计算最大输入长度，超出输入无效。使用pattern时无需在xml中设置maxLength属性，若需要设置时应注意加上分隔符的数量
    private int maxLength;
    private boolean hasNoSeparator; // 设置为true时功能同EditText

    public XEditText(Context context) {
        this(context, null);
    }

    public XEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle); // Attention !
    }

    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (separator == null)
            separator = SPACE;
        init();
    }



    private void init() {
        // 如果设置 inputType="number" 的话是没法插入空格的，所以强行转为inputType="phone"
        if (getInputType() == InputType.TYPE_CLASS_NUMBER)
            setInputType(InputType.TYPE_CLASS_PHONE);
        setPattern(DEFAULT_PATTERN);
        mTextWatcher = new MyTextWatcher();
        this.addTextChangedListener(mTextWatcher);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    /**
     * 自定义分隔符
     */
    public void setSeparator(String separator) {
        if (separator == null) {
            throw new IllegalArgumentException("separator can't be null !");
        }
        this.separator = separator;
    }



    /**
     * 自定义分割模板
     * @param pattern 每一段的字符个数的数组
     */
    public void setPattern(int[] pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern can't be null !");
        }
        this.pattern = pattern;
        intervals = new int[pattern.length];
        int count = 0;
        int sum = 0;
        for (int i = 0; i < pattern.length; i++) {
            sum += pattern[i];
            intervals[i] = sum + count;
            if (i < pattern.length - 1)
                count++;
        }
        maxLength = intervals[intervals.length - 1];

    }



    /**
     * 输入待转换格式的字符串
     */
    public void setTextToSeparate(CharSequence c) {
        if (c == null || c.length() == 0)
            return;
        setText("");
        for (int i = 0; i < c.length(); i++) {
            append(c.subSequence(i, i + 1));
        }
    }



    /**
     * 获得除去分割符的输入框内容
     */
    public String getNonSeparatorText() {
        return getText().toString().replaceAll(separator, "");
    }


    /**
     * @return 是否有分割符
     */
    public boolean hasNoSeparator() {
        return hasNoSeparator;
    }

    /**
     * @param hasNoSeparator true设置无分隔符模式，功能同EditText
     */
    public void setHasNoSeparator(boolean hasNoSeparator) {
        this.hasNoSeparator = hasNoSeparator;
        if (hasNoSeparator)
            separator = "";
    }

    /**
     * 设置OnTextChangeListener，同EditText.addOnTextChangeListener()
     */
    public void setOnTextChangeListener(OnTextChangeListener listener) {
        this.mTextChangeListener = listener;
    }


    // =========================== MyTextWatcher===========================
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            preLength = s.length();
            if (mTextChangeListener != null)
                mTextChangeListener.beforeTextChanged(s, start, count, after);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            currLength = s.length();
            if (hasNoSeparator)
                maxLength = currLength;
            if (currLength > maxLength) {
                getText().delete(currLength - 1, currLength);
                return;
            }
            for (int i = 0; i < pattern.length; i++) {
                if (currLength == intervals[i]) {
                    if (currLength > preLength) { // 正在输入
                        if (currLength < maxLength) {
                            removeTextChangedListener(mTextWatcher);
                            mTextWatcher = null;
                            getText().insert(currLength, separator);
                        }
                    } else if (preLength <= maxLength) { // 正在删除
                        removeTextChangedListener(mTextWatcher);
                        mTextWatcher = null;
                        getText().delete(currLength - 1, currLength);
                    }
                    if (mTextWatcher == null) {
                        mTextWatcher = new MyTextWatcher();
                        addTextChangedListener(mTextWatcher);
                    }
                    break;
                }
            }
            if (mTextChangeListener != null)
                mTextChangeListener.onTextChanged(s, start, before, count);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mTextChangeListener != null)
                mTextChangeListener.afterTextChanged(s);
        }
    }


    public interface OnTextChangeListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
        void onTextChanged(CharSequence s, int start, int before, int count);
        void afterTextChanged(Editable s);
    }

}