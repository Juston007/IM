package com.im.aty;

import com.im.db.IMDBUtil;
import com.im.model.UserInfoModel;
import com.im.util.Paramters;
import com.im.util.SharedPreferencesUtil;
import com.im.view.MyDialog;
import com.im.view.ToastUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MeActivity extends Fragment implements OnClickListener {

	// View
	private View view;
	private EditText et = null;
	private MyDialog dialog;
	private TextView txtServerUrl, txtConnectionTimeOut, txtReadTimeOut,
			txtPollingTimeSpan, txtUserName, txtUid;
	private ImageView imgFace;
	private Bitmap face;

	// ����¼�
	OnClickListener cancelClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			dialog.dismiss();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_me, null);
		this.view = view;
		// ��ʼ��View
		initView();
		// ��ʼ���¼�
		initEvent();
		// ��ȡ��������ʾ
		readParamter();
		// չʾ�û���Ϣ
		displayUserInfo();
		Log.d("IMClient", MeActivity.class.getCanonicalName() + ":��ʼ�����");
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// չʾ�û���Ϣ
		Log.d("IMClient", "onResume");
		displayUserInfo();
	}

	// ��ȡ����
	private void readParamter() {
		txtServerUrl.setText(String.valueOf(Paramters.ServerHostAddress));
		txtConnectionTimeOut.setText(String
				.valueOf(Paramters.ConnectionTimeOut));
		txtReadTimeOut.setText(String.valueOf(Paramters.ReadTimeOut));
		txtPollingTimeSpan.setText(String.valueOf(Paramters.PollingTimeSpan));
	}

	private void displayUserInfo() {
		// ��ѯ��ǰUidȻ���ѯ����ǰ�û���Ϣ
		String uid = SharedPreferencesUtil.shared.getUid();
		UserInfoModel model = IMDBUtil.getUserInfo(uid);
		// ���û���Ϣչʾ����
		txtUserName.setText(model.getAlias());
		txtUid.setText(model.getUid());
		face = BitmapFactory.decodeFile(model.getFacePath());
		imgFace.setImageBitmap(face);
	}

	// ��ʼ��View
	private void initView() {
		txtServerUrl = (TextView) view.findViewById(R.id.txt_Me_ServerUrl);
		txtConnectionTimeOut = (TextView) view
				.findViewById(R.id.txt_Me_Connection_Time_Out);
		txtReadTimeOut = (TextView) view
				.findViewById(R.id.txt_Me_Read_Time_Out);
		txtPollingTimeSpan = (TextView) view
				.findViewById(R.id.txt_Me_Polling_Time_Span);
		txtUserName = (TextView) view.findViewById(R.id.txt_Me_User_Name);
		txtUid = (TextView) view.findViewById(R.id.txt_Me_Uid);
		imgFace = (ImageView) view.findViewById(R.id.img_Me_User_Face);
	}

	// ��ʼ���¼�
	private void initEvent() {
		view.findViewById(R.id.ll_Me_Aboutme).setOnClickListener(this);
		view.findViewById(R.id.ll_Connection_Time_Out).setOnClickListener(this);
		view.findViewById(R.id.ll_Read_Time_Out).setOnClickListener(this);
		view.findViewById(R.id.ll_Polling_Time_Span).setOnClickListener(this);
		view.findViewById(R.id.ll_ServerUrl).setOnClickListener(this);
		view.findViewById(R.id.ll_Me_About).setOnClickListener(this);
		view.findViewById(R.id.ll_Exit).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		et = new EditText(getActivity());
		et.setInputType(InputType.TYPE_CLASS_NUMBER);
		et.setTextColor(0xFF000000);

		switch (arg0.getId()) {
		// ��ת���������Ͻ���
		case R.id.ll_Me_Aboutme:
			Intent intent = new Intent(getActivity(), MeInfoActivity.class);
			startActivity(intent);
			break;
		// �������ӳ�ʱʱ��
		case R.id.ll_Connection_Time_Out:
			dialog = new MyDialog(getActivity(), "���ӳ�ʱʱ��", "����", "ȡ��",
					String.valueOf(Paramters.ConnectionTimeOut), true);
			dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
			dialog.setCancelOnClickListener(cancelClick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals(""))
						ToastUtil.makeText(getActivity(), "������Ϊ��",
								Toast.LENGTH_SHORT);
					else {
						SharedPreferencesUtil.shared.writeValue(
								"ConnectionTimeOut", str);
						ToastUtil.makeText(getActivity(), "�������óɹ���������Ч!",
								Toast.LENGTH_SHORT, true);
						dialog.dismiss();
					}
				}
			});
			dialog.show();
			break;
		// ���ö�ȡ��ʱʱ��
		case R.id.ll_Read_Time_Out:
			dialog = new MyDialog(getActivity(), "��ȡ��ʱʱ��", "����", "ȡ��",
					String.valueOf(Paramters.ReadTimeOut), true);
			dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
			dialog.setCancelOnClickListener(cancelClick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals(""))
						ToastUtil.makeText(getActivity(), "������Ϊ��",
								Toast.LENGTH_SHORT);
					else {
						SharedPreferencesUtil.shared.writeValue("ReadTimeOut",
								str);
						ToastUtil.makeText(getActivity(), "�������óɹ���������Ч!",
								Toast.LENGTH_SHORT, true);
						dialog.dismiss();
					}
				}
			});
			dialog.show();
			break;
		// ������ѯʱ����
		case R.id.ll_Polling_Time_Span:
			dialog = new MyDialog(getActivity(), "��ѯʱ����", "����", "ȡ��",
					String.valueOf(Paramters.PollingTimeSpan), true);
			dialog.setInputType(InputType.TYPE_CLASS_NUMBER);
			dialog.setCancelOnClickListener(cancelClick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals(""))
						ToastUtil.makeText(getActivity(), "������Ϊ��",
								Toast.LENGTH_SHORT);
					else {
						SharedPreferencesUtil.shared.writeValue(
								"PollingTimeSpan", str);
						ToastUtil.makeText(getActivity(), "�������óɹ���������Ч!",
								Toast.LENGTH_SHORT, true);
						dialog.dismiss();
					}
				}
			});
			dialog.show();
			break;
		// ���÷�������ַ
		case R.id.ll_ServerUrl:
			dialog = new MyDialog(getActivity(), "��������ַ", "����", "ȡ��",
					String.valueOf(Paramters.ServerHostAddress), true);
			dialog.setCancelOnClickListener(cancelClick);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = dialog.getEdText();
					if (str.equals(""))
						ToastUtil.makeText(getActivity(), "������Ϊ��",
								Toast.LENGTH_SHORT);
					else {
						SharedPreferencesUtil.shared.writeValue(
								"ServerHostAddress", str);
						ToastUtil.makeText(getActivity(), "�������óɹ���������Ч!",
								Toast.LENGTH_SHORT, true);
						dialog.dismiss();
					}
				}
			});
			dialog.show();
			break;
		// �˳�
		case R.id.ll_Exit:
			dialog = new MyDialog(getActivity(), "�˳�", "��Ҫ�ǳ����˺���",
					"�ǳ��˺�", "ȡ��", true, true);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					getActivity().finish();
					SharedPreferencesUtil.shared.removeUser();
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
			});
			dialog.setCancelOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		// ���ڿ�����
		case R.id.ll_Me_About:
			final MyDialog dialog = new MyDialog(getActivity(), "���ڿ�����",
					"����\nVersion 1.0.0", "ȷ��", "ȡ��", true, false);
			dialog.setConfrimOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
				}
			});
			dialog.show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (face != null)
			face.recycle();
	}
}
