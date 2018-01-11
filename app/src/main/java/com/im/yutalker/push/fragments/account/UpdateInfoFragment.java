package com.im.yutalker.push.fragments.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.im.yutalker.common.app.Application;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.com.UploadHelper;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends Fragment {
    @BindView(R.id.im_portrait)
    PortraitView portraitView;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    public void onPortraitClick() {
        new GalleryFragment().setListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImage(String path) {
                UCrop.Options options = new UCrop.Options();
                // 设置图片处理格式JPEG
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                // 设置压缩后的图片精度
                options.setCompressionQuality(96);
                // 设置图片在切换比例时的动画
                options.setImageToCropBoundsAnimDuration(666);
                // 设置状态栏颜色
                options.setStatusBarColor(getResources().getColor(R.color.colorAccentAlpha));
                // 设置标题栏颜色
                options.setToolbarColor(getResources().getColor(R.color.colorAccentAlpha));
                // 设置控件点击颜色
                options.setActiveWidgetColor(getResources().getColor(R.color.blue_400));
                // 设置裁剪外边框颜色
//                options.setCropFrameColor(getResources().getColor(R.color.white));
                // 设置裁剪网格颜色
//                options.setCropGridColor(getResources().getColor(R.color.white));
                // 设置裁剪背景颜色
//                options.setDimmedLayerColor(getResources().getColor(R.color.white));


                // 得到头像缓存地址
                File dPath = Application.getPortraitTmpFile();

                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                        .useSourceImageAspectRatio()// 图片原始宽高比
//                        .withAspectRatio(1, 1) // 1比1比例
                        .withMaxResultSize(520, 520) // 返回最大的尺寸
                        .withOptions(options) // 相关参数
                        .start(getActivity());
            }
            // show() 是BottomSheetDialogFragment自带的方法，建议用getChildFragmentManager
            // tag GalleryFragment.class 名
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 收到从Activity中传递过来的回调，然后取出值进行加载图片
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //得到对应的uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                LoadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    /**
     * 加载到当前的头像中
     *
     * @param uri Uri
     */
    private void LoadPortrait(Uri uri) {
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(portraitView);

        // 拿到本地文件的地址
        final String localPath = uri.getPath();
        Log.e("TAG", "localPath:" + localPath);
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                String url=UploadHelper.uploadPortrait(localPath);
                Log.e("TAG", "url:" + url);
            }
        });
    }

}
