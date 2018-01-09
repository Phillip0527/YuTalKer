package com.im.yutalker.push.fragments.main;

import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.widget.GalleryView;
import com.im.yutalker.push.R;

import butterknife.BindView;


public class ActiveFragment extends Fragment {
//    private static final int REQUEST_EXTERNAL_STORAGE = 1;// 权限标识
//    private static String[] PERMISSIONS_STORAGE = {
//            "android.permission.READ_EXTERNAL_STORAGE",
//            "android.permission.WRITE_EXTERNAL_STORAGE"}; // 需要开启的危险权限

    @BindView(R.id.galleryView)
    GalleryView mGalley;

    public ActiveFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initData() {
        super.initData();

        mGalley.setup(getLoaderManager(), new GalleryView.SelectedChangeListener() {
            @Override
            public void onSelectedCountChanged(int count) {

            }
        });



    }


    //        //检测是否有读写的权限
//        int permission = ActivityCompat.checkSelfPermission(getActivity(),
//                "android.permission.WRITE_EXTERNAL_STORAGE");
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // 没有写的权限，去申请写的权限，会弹出对话框
//            ActiveFragment.this.requestPermissions(PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//        } else {
//            setup();
//        }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
//                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                setup();
//            } else {
//                Toast.makeText(getActivity(), "读写权限不可用，请启用权限！", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

}
