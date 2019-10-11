package com.example.jingbin.cloudreader.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.jingbin.cloudreader.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class IMChatDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "IMChatDialog";

    private TextView dialog_title;//标题
    private TextView dialog_text;//内容
    private EditText dialog_edit;//可编辑内容
    private Button cancel, determine;//取消，确认按钮
    private RelativeLayout progress_layout;//进度条布局
    private ProgressBar progress;
    private TextView progress_percent,progress_number;


    //    private String cancelStr, determineStr;//按钮文字
    private String title;
    private String msg;
    private IMChatDialogUser user;
    private boolean showCancel;//是否显示取消按钮
    private boolean isEdit;//是否可以编辑
    private boolean isVersion;//是否是版本升级(进度条)
    private boolean isWordNumber;//是否限定输入框的字数
    private boolean isDefaultText;//是否有默认字符(针对震功能弹框)

    public interface IMChatDialogUser {//回调接口

        void onResult(boolean confirmed);
    }


    /**
     * 版本升级
     * @param context
     */
    public IMChatDialog(Context context, String title, String msg) {
        super(context);
        this.title = title;
        this.msg = msg;
        isVersion=true;
        this.setCanceledOnTouchOutside(true);
    }

    public IMChatDialog(Context context, String title, String msg, boolean isShowCancel, boolean isEdit) {
        super(context);
        this.title = title;
        this.msg = msg;
        this.showCancel = isShowCancel;
        this.isEdit = isEdit;
        this.setCanceledOnTouchOutside(true);
    }



    public IMChatDialog(Context context, String title, String msg, boolean isShowCancel, boolean isEdit, boolean isWordNumber) {
        super(context);
        this.title = title;
        this.msg = msg;
        this.showCancel = isShowCancel;
        this.isEdit = isEdit;
        this.isWordNumber = isWordNumber;
        this.setCanceledOnTouchOutside(true);
    }

    public IMChatDialog(Context context, String title, boolean isShowCancel, boolean isEdit, boolean isWordNumber, boolean isDefaultText) {
        super(context);
        this.title = title;
        this.showCancel = isShowCancel;
        this.isEdit = isEdit;
        this.isWordNumber = isWordNumber;
        this.isDefaultText = isDefaultText;
        this.setCanceledOnTouchOutside(true);
    }

    public void setClickCallback( IMChatDialogUser user){
        this.user = user;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update);


        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
    }


    private void initView() {
        dialog_title = findViewById(R.id.dialog_title);
        dialog_edit = findViewById(R.id.dialog_edit);
        dialog_text = findViewById(R.id.dialog_text);
        cancel = findViewById(R.id.cancel);
        determine = findViewById(R.id.determine);
        progress_layout = findViewById(R.id.progress_layout);
        progress = findViewById(R.id.progress);
//        determine = findViewById(R.id.determine);
        progress_percent = findViewById(R.id.progress_percent);
        progress_number = findViewById(R.id.progress_number);
    }

    private void initData() {
        if(isVersion){//显示进度条
            progress_layout.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            determine.setVisibility(View.GONE);
            dialog_title.setText(title);
            dialog_text.setText(msg);

        }else{
            dialog_title.setText(title);
            cancel.setVisibility(showCancel ? View.VISIBLE : View.GONE);

            if (isEdit) {
                dialog_text.setVisibility(View.GONE);
                dialog_edit.setVisibility(View.VISIBLE);

                if(isDefaultText){//设置默认字符(针对震功能)
                    dialog_edit.setHint("填写震的内容或点击确认直接震");
                }else{
                    dialog_edit.setText(msg);
                    dialog_edit.setSelection(dialog_edit.getText().length());
                }



                setInputFilter();

            } else {
                dialog_text.setText(msg);
            }
        }
    }



    private void setInputFilter() {
        //过滤器(禁止输入表情)
        InputFilter inputFilter= new InputFilter() {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Matcher emojiMatcher = emoji.matcher(source);
                if (emojiMatcher.find()) {
                    return "";
                }
                return null;
            }
        };


        //给editText设置filter   设置了两个filter第一个是屏蔽表情，第二个是设置用户输入多少字数的限制
        if(isWordNumber){
            if(isDefaultText){//震消息
                dialog_edit.setFilters(new InputFilter[]{inputFilter,new InputFilter.LengthFilter(50)});
            }else{
                dialog_edit.setFilters(new InputFilter[]{inputFilter,new InputFilter.LengthFilter(20)});
            }
        }else{
            dialog_edit.setFilters(new InputFilter[]{inputFilter,new InputFilter.LengthFilter(10)});
        }
    }

    private void initEvent() {
        cancel.setOnClickListener(this);
        determine.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.cancel) {
            onCancel(v);//取消
        } else if (i == R.id.determine) {
            onOk(v);//确认
        }
    }

    public void onOk(View view) {
        this.dismiss();
        if (this.user != null) {
            this.user.onResult(true);
        }
    }

    public void onCancel(View view) {
        this.dismiss();
        if (this.user != null) {
            this.user.onResult(false);
        }
    }

    public void setTitleStr(String title) {
        dialog_title.setText(title);
    }

    public void setEditStr(String edit) {
        if(null!=dialog_edit){
            dialog_edit.setText(edit);
            dialog_edit.setSelection(dialog_edit.getText().length());
        }
    }


    public void setEditHintStr(String edit) {
        if(null!=dialog_edit){
            dialog_edit.setHint(edit);
        }
    }

    public void setTextStr(String text) {
        dialog_text.setText(text);
    }

    public void setDetermineStr(String determineStr) {
        determine.setText(determineStr);
    }

    public void setCancelStr(String cancelStr) {
        cancel.setText(cancelStr);
    }

    public void setTextGravity(int gravity) {
        if (null != dialog_text) {
            dialog_text.setGravity(gravity);
        }
    }

    public void setShowEdit(String edit){
        dialog_text.setVisibility(View.GONE);
        dialog_edit.setVisibility(View.VISIBLE);
        dialog_edit.setText(edit);
        dialog_edit.setSelection(dialog_edit.getText().length());
    }

    public void setShowtText(String text){
        dialog_edit.setVisibility(View.GONE);
        dialog_text.setVisibility(View.VISIBLE);
        dialog_text.setText(text);
    }

    public void setProgressValue(int progressValue){
        if(null!=progress){
            progress.setProgress(progressValue);
        }
    }

    public void setCurrentAndPopulation(String value){
        if(null!=progress_number){
            progress_number.setText(value);
        }
    }

    public void setCurrentValue(String value){
        if(null!=progress_percent){
            progress_percent.setText(value);
        }
    }

    public String getEditText(){
        String content= dialog_edit.getText().toString();
        if(null==content)content="";
        return  content;
    }
}
