package com.li.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.li.R;

public class PickDialog extends Dialog {
    private Context context;
    private String title;
    private ImageView weixin_im;

    public PickDialog(Context context, String title) {
        super(context, R.style.blend_theme_dialog);
        this.context = context;
        this.title = title;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.dialog_qingdan_preview_layout, null);

        TextView titleTextview = (TextView) layout.findViewById(R.id.blend_dialog_title);
        titleTextview.setText(title);
        weixin_im = (ImageView) layout.findViewById(R.id.weixin_im);
        TextView cancleTextView = (TextView) layout.findViewById(R.id.blend_dialog_cancle_btn);
        this.setCanceledOnTouchOutside(true);
        this.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });
        cancleTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dismiss();

            }


        });
        weixin_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.weixin);

            }
        });

        this.setContentView(layout);
    }

//    public void saveBitmap() throws IOException {

//        File f = new File("/Musics/juanzhu/","picName");
//        if (!f.exists() && !f.isDirectory()) {
//            f.mkdirs();
//        }
//        if (f.exists()) {
//            f.delete();
//        }
//        try {
//            FileOutputStream out = new FileOutputStream(f);
//            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//            LogUtil.m("保存成功");
//            Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


//    }


}
