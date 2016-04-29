package cn.appleye.randomcontact;

import android.app.Fragment;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import cn.appleye.randomcontact.common.model.BaseContactType;
import cn.appleye.randomcontact.utils.SettingsUtils;
import cn.appleye.randomcontact.widget.CheckableTextView;
import cn.appleye.randomcontact.widget.ProgressDialogEx;

/**
 * Created by iSpace on 2016/3/19.
 */
public class GenerateFragment extends Fragment implements Handler.Callback {
    private EditText mCountsView;
    private CheckableTextView mMultiNumberCheckbox;
    private CheckableTextView mSameRepeatCheckbox;
    private Button mResetBtn;
    private Button mOKBtn;

    private boolean mIsMultiNumberAllowed = false;
    private boolean mIsSameContactRepeat = false;

    private ProgressDialogEx mLoadingDialog = null;
    private boolean mCancel = false;
    private Handler mMainHandler = new Handler(this);

    private static final int MESSAGE_GENERATE_SUCCESS = 1;
    private static final int MESSAGE_GENERATE_FAILED = 0;

    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflateView(inflater, container);
    }

    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.activity_generate_contacts, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();

        View rootView = getView();
        mCountsView = (EditText)rootView.findViewById(R.id.contacts_count);
        mMultiNumberCheckbox = (CheckableTextView)rootView.findViewById(R.id.multi_numbers_checkbox);
        mSameRepeatCheckbox = (CheckableTextView)rootView.findViewById(R.id.same_repeat_checkbox);
        mResetBtn = (Button)rootView.findViewById(R.id.reset_btn);
        mOKBtn = (Button)rootView.findViewById(R.id.ok_btn);

        setEventListener();
    }

    /**
     * 添加点击事件
     * */
    private void setEventListener() {
        mMultiNumberCheckbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMultiNumberCheckbox.toggole();
            }
        });

        mSameRepeatCheckbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mSameRepeatCheckbox.toggole();
            }
        });

        mResetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetItems();
            }
        });

        mOKBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String countText = mCountsView.getText().toString();

                if (TextUtils.isEmpty(countText)) {
                    Toast.makeText(mContext, R.string.toast_empty_counts, Toast.LENGTH_SHORT).show();
                } else if (!countText.matches("[1-9][0-9]{0,5}")) {
                    Toast.makeText(mContext, R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
                } else {
                    mIsMultiNumberAllowed = mMultiNumberCheckbox.isChecked();
                    mIsSameContactRepeat = mSameRepeatCheckbox.isChecked();

                    startGenerate(countText);
                }
            }
        });
    }

    private void resetItems() {
        mCountsView.setText("");
        mMultiNumberCheckbox.setChecked(false);
        mSameRepeatCheckbox.setChecked(false);
    }

    /**
     * 根据设置选项，生成联系人信息，默认生成所有信息
     * 所有联系人默认生成姓名、号码、头像
     * */
    private BaseContactType createBaseContactType() {
        BaseContactType baseContactType = new BaseContactType();
        baseContactType.clear();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean value;

        value = pref.getBoolean(SettingsUtils.PRE_KEY_DISPLAY_NAME, true);
        if (value) {
            baseContactType.addDataKindStructuredName();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_PNONE_NUMBER, true);
        if (value) {
            baseContactType.addDataKindPhone(mIsMultiNumberAllowed?3:1);
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_PHOTO, false);
        if (value) {
            baseContactType.addDataKindPhoto();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_EVENT, false);
        if (value) {
            baseContactType.addDataKindEvent();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_EMAIL, false);
        if (value) {
            baseContactType.addDataKindEmail();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_IM, false);
        if (value) {
            baseContactType.addDataKindIm();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_NICK_NAME, false);
        if (value) {
            baseContactType.addDataKindNickname();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_NOTE, false);
        if (value) {
            baseContactType.addDataKindNote();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_ORG, false);
        if (value) {
            baseContactType.addDataKindOrganization();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_WEBSITE, false);
        if (value) {
            baseContactType.addDataKindWebsite();
        }

        value = pref.getBoolean(SettingsUtils.PRE_KEY_POSTAL, false);
        if (value) {
            baseContactType.addDataKindStructuredPostal();
        }

        return baseContactType;
    }

    /**
     * 开始生成随机联系人
     * */
    private void startGenerate(String countText) {
        try{
            final BaseContactType baseContactType = createBaseContactType();

            if(baseContactType.getDataKindSize() <= 0) {
                Toast.makeText(mContext, R.string.nothing_generate, Toast.LENGTH_SHORT).show();
                return;
            }

            final int count = Integer.parseInt(countText);

            showLoadingDialog();

            mLoadingDialog.setMax(count);

            mCancel = false;

            new Thread() {
                public void run() {
                    ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                    ArrayList<ContentProviderOperation> perOperationList = null;

                    if (mIsSameContactRepeat) {//只生成完全相同联系人
                        ArrayList<Long> rawContactIds = new ArrayList<Long>();
                        int realCount = 0;
                        for (int i=0; i< count; i++) {
                            Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                            long rawContactId = ContentUris.parseId(mContext.getContentResolver().insert(uri, new ContentValues()));
                            rawContactIds.add(rawContactId);

                            realCount++;
                            if (mCancel) {
                                break;
                            }
                        }

                        perOperationList = baseContactType.buildRepeatContentValues(rawContactIds, true);

                        int processedCount = 0, step = (int)(perOperationList.size()/realCount);
                        for (ContentProviderOperation operation : perOperationList) {
                            operationList.add(operation);

                            if (operationList.size() >= 498) {//批处理一次最多500个
                                try{
                                    mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                                    operationList.clear();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                mLoadingDialog.setProgress(processedCount/step);
                            }

                            processedCount ++ ;
                        }

                        if (operationList.size() != 0) {
                            try{
                                mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                                operationList.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            processedCount = processedCount+operationList.size();
                            mLoadingDialog.setProgress(realCount);
                        }
                    } else {
                        for (int i=0; i< count; i++) {
                            Uri uri = ContactsContract.RawContacts.CONTENT_URI;
                            long rawContactId = ContentUris.parseId(mContext.getContentResolver().insert(uri, new ContentValues()));

                            perOperationList = baseContactType.buildContentValues(mContext, rawContactId, true);

                            if (operationList.size() + perOperationList.size() >= 500) {
                                try{
                                    mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                                    operationList.clear();

                                    mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, perOperationList);
                                    perOperationList.clear();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                operationList.addAll(perOperationList);
                            }

                            if (i%4 == 0) {
                                mLoadingDialog.setProgress(i+1);
                            }

                            if (mCancel) {
                                break;
                            }
                        }
                    }

                    if (!mCancel && operationList.size() > 0) {
                        try{
                            mContext.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                            operationList.clear();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mLoadingDialog.setProgress(count);
                    }

                    if (mCancel) {
                        mMainHandler.sendEmptyMessage(MESSAGE_GENERATE_FAILED);
                    } else {
                        mMainHandler.sendEmptyMessage(MESSAGE_GENERATE_SUCCESS);
                    }
                }
            }.start();
        }catch(NumberFormatException nfe) {
            Toast.makeText(mContext, R.string.toast_invalid_number, Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoadingDialog(){
        if(mLoadingDialog == null){
            mLoadingDialog = new ProgressDialogEx(mContext);
            mLoadingDialog.setTitle(R.string.loading_dialog_title);
            mLoadingDialog.setMessage(getString(R.string.loading_dialog_message));
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setProgressStyle(ProgressDialogEx.STYLE_HORIZONTAL);
            mLoadingDialog.show();

        }else if(mLoadingDialog!=null && !mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }

    private void dismissLoadingDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }

    public boolean onBackPressed() {
        if (mLoadingDialog!=null && mLoadingDialog.isShowing()) {
            mCancel = true;

            return false;
        }

        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        int what = msg.what;

        if (MESSAGE_GENERATE_SUCCESS == what) {
            Toast.makeText(mContext, R.string.message_generate_finish, Toast.LENGTH_SHORT).show();
            resetItems();
            dismissLoadingDialog();
            return true;
        } else if (MESSAGE_GENERATE_FAILED == what) {
            Toast.makeText(mContext, R.string.message_generate_cancel, Toast.LENGTH_SHORT).show();
            resetItems();
            dismissLoadingDialog();
            return true;
        }

        return false;
    }
}
