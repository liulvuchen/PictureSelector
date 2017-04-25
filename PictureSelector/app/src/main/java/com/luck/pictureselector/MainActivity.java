package com.luck.pictureselector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.luck.picture.lib.compress.Luban;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.FunctionOptions;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.luck.pictureselector.adapter.GridImageAdapter;
import com.luck.pictureselector.util.FullyGridLayoutManager;
import com.yalantis.ucrop.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：16/12/31
 */

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    //选择事件
    private RadioGroup rgbs01, rgbs0, rgbs1, rgbs2, rgbs3, rgbs4, rgbs5, rgbs6, rgbs7, rgbs8, rgbs9, rgbs10;
    public static final String TAG = "MainActivity";
    //android:overScrollMode="never"设置RecyclerView的拉伸底部不出现
    private RecyclerView recyclerView;
    //recyclerView的适配器
    private GridImageAdapter adapter;
    //单选设置
    private int selectMode = FunctionConfig.MODE_MULTIPLE;
    // 图片最大可选数量
    private int maxSelectNum = 9;
    //图片的数量选择
    private ImageButton minus, plus;
    private EditText select_num;
    //裁剪的宽度和高度和压缩的宽高
    private EditText et_w, et_h, et_compress_width, et_compress_height;
    //压缩图片的显示和隐藏
    private LinearLayout ll_luban_wh;
    //是否显示拍照选项 这里自动根据type 启动拍照或录视频
    private boolean isShow = true;
    // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
    private int selectType = LocalMediaLoader.TYPE_IMAGE;
    //裁剪比例，默认、1:1、3:4、3:2、16:9
    private int copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
    // 是否打开预览选项
    private boolean enablePreview = true;
    // 是否预览视频(播放) mode or 多选有效
    private boolean isPreviewVideo = true;
    // 是否打开剪切选项
    private boolean enableCrop = true;
    // 主题颜色
    private boolean theme = false;
    // 选择图片的样式（普通模式）
    private boolean selectImageType = false;
    // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
    private int cropW = 0;
    // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
    private int cropH = 0;
    // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
    private int maxB = 0;
    // 压缩宽 如果值大于图片原始宽高无效
    private int compressW = 0;
    // 压缩高 如果值大于图片原始宽高无效
    private int compressH = 0;
    //是否压缩
    private boolean isCompress = false;
    // 选择图片的样式（qq模式）
    private boolean isCheckNumMode = false;
    // 1 系统自带压缩 2 luban压缩
    private int compressFlag = 1;
    // 图片选中实体类
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private Context mContext;
    // 压缩最大大小 单位是b
    private EditText et_kb;
    // 主题颜色
    private int themeStyle;
    /*
    * PreviewColor 预览文字颜色
    * CompleteColor 完成文字颜色
    * PreviewBottomBgColor 预览界面底部背景色
    * BottomBgColor 选择图片页面底部背景色
    * checkedBoxDrawable 图片勾选样式
    * */
    private int previewColor, completeColor, previewBottomBgColor, bottomBgColor, checkedBoxDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        /*
        * 初始化控件
        * */
        mContext = this;
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        rgbs01 = (RadioGroup) findViewById(R.id.rgbs01);
        rgbs0 = (RadioGroup) findViewById(R.id.rgbs0);
        rgbs1 = (RadioGroup) findViewById(R.id.rgbs1);
        rgbs2 = (RadioGroup) findViewById(R.id.rgbs2);
        rgbs3 = (RadioGroup) findViewById(R.id.rgbs3);
        rgbs4 = (RadioGroup) findViewById(R.id.rgbs4);
        rgbs5 = (RadioGroup) findViewById(R.id.rgbs5);
        rgbs6 = (RadioGroup) findViewById(R.id.rgbs6);
        rgbs7 = (RadioGroup) findViewById(R.id.rgbs7);
        rgbs8 = (RadioGroup) findViewById(R.id.rgbs8);
        rgbs9 = (RadioGroup) findViewById(R.id.rgbs9);
        et_kb = (EditText) findViewById(R.id.et_kb);
        rgbs10 = (RadioGroup) findViewById(R.id.rgbs10);
        findViewById(R.id.left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ll_luban_wh = (LinearLayout) findViewById(R.id.ll_luban_wh);
        et_compress_width = (EditText) findViewById(R.id.et_compress_width);
        et_compress_height = (EditText) findViewById(R.id.et_compress_height);
        et_w = (EditText) findViewById(R.id.et_w);
        et_h = (EditText) findViewById(R.id.et_h);
        minus = (ImageButton) findViewById(R.id.minus);
        plus = (ImageButton) findViewById(R.id.plus);
        select_num = (EditText) findViewById(R.id.select_num);
        //设置选中的数量
        select_num.setText(maxSelectNum + "");
        //初始化adapter
        FullyGridLayoutManager manager = new FullyGridLayoutManager(MainActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(MainActivity.this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        //设置监听和选择图片数量的加减
        rgbs0.setOnCheckedChangeListener(this);
        rgbs1.setOnCheckedChangeListener(this);
        rgbs2.setOnCheckedChangeListener(this);
        rgbs3.setOnCheckedChangeListener(this);
        rgbs4.setOnCheckedChangeListener(this);
        rgbs5.setOnCheckedChangeListener(this);
        rgbs6.setOnCheckedChangeListener(this);
        rgbs7.setOnCheckedChangeListener(this);
        rgbs8.setOnCheckedChangeListener(this);
        rgbs9.setOnCheckedChangeListener(this);
        rgbs01.setOnCheckedChangeListener(this);
        rgbs10.setOnCheckedChangeListener(this);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                select_num.setText(maxSelectNum + "");
                //设置adapter中最大筛选的数量
                adapter.setSelectMax(maxSelectNum);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxSelectNum < 9) {
                    maxSelectNum++;
                }
                select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
            }
        });

        //adapter项监听事件
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                switch (selectType) {
                    case FunctionConfig.TYPE_IMAGE:
                        // 预览图片
                        PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, selectMedia);
                        break;
                    case FunctionConfig.TYPE_VIDEO:
                        // 预览视频
                        PictureConfig.getPictureConfig().externalPictureVideo(mContext, selectMedia.get(position).getPath());
                        break;
                }

            }
        });
    }

    /**
     * 删除图片回调接口
     */
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    // 进入相册
                    /**
                     * type --> 1图片 or 2视频
                     * copyMode -->裁剪比例，默认、1:1、3:4、3:2、16:9
                     * maxSelectNum --> 可选择图片的数量
                     * selectMode         --> 单选 or 多选
                     * isShow       --> 是否显示拍照选项 这里自动根据type 启动拍照或录视频
                     * isPreview    --> 是否打开预览选项
                     * isCrop       --> 是否打开剪切选项
                     * isPreviewVideo -->是否预览视频(播放) mode or 多选有效
                     * ThemeStyle -->主题颜色
                     * CheckedBoxDrawable -->图片勾选样式
                     * cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                     * cropH-->裁剪高度 值不能小于100
                     * isCompress -->是否压缩图片
                     * setEnablePixelCompress 是否启用像素压缩
                     * setEnableQualityCompress 是否启用质量压缩
                     * setRecordVideoSecond 录视频的秒数，默认不限制
                     * setRecordVideoDefinition 视频清晰度  Constants.HIGH 清晰  Constants.ORDINARY 低质量
                     * setImageSpanCount -->每行显示个数
                     * setCheckNumMode 是否显示QQ选择风格(带数字效果)
                     * setPreviewColor 预览文字颜色
                     * setCompleteColor 完成文字颜色
                     * setPreviewBottomBgColor 预览界面底部背景色
                     * setBottomBgColor 选择图片页面底部背景色
                     * setCompressQuality 设置裁剪质量，默认无损裁剪
                     * setSelectMedia 已选择的图片
                     * setCompressFlag 1为系统自带压缩  2为第三方luban压缩
                     * 注意-->type为2时 设置isPreview or isCrop 无效
                     * 注意：Options可以为空，默认标准模式
                     */
                    //裁剪的宽度和高度
                    /*trim() 函数的功能是去掉首尾空格，在编程中发现使用if(EditText02.getText().toString()!="")不能正确判断是否为空，调试后发现是莫名空格的原因，使用trim()函数后解决。*/
                    String ws = et_w.getText().toString().trim();
                    String hs = et_h.getText().toString().trim();
                    String b = et_kb.getText().toString().trim();// 压缩最大大小 单位是b

                    if (!isNull(ws) && !isNull(hs)) {
                        cropW = Integer.parseInt(ws);
                        cropH = Integer.parseInt(hs);
                    }
                    if (!isNull(b)) {
                        maxB = Integer.parseInt(b);
                    }

                    if (!isNull(et_compress_width.getText().toString()) && !isNull(et_compress_height.getText().toString())) {
                        compressW = Integer.parseInt(et_compress_width.getText().toString());
                        compressH = Integer.parseInt(et_compress_height.getText().toString());
                    }

                    if (theme) {
                        // 设置主题样式（相册中的主题样式）
                        themeStyle = ContextCompat.getColor(MainActivity.this, R.color.blue);
                    } else {
                        themeStyle = ContextCompat.getColor(mContext, R.color.bar_grey);
                    }

                    if (isCheckNumMode) {
                        // QQ 风格模式下 这里自己搭配颜色
                        previewColor = ContextCompat.getColor(MainActivity.this, R.color.blue);
                        completeColor = ContextCompat.getColor(MainActivity.this, R.color.blue);
                    } else {
                        previewColor = ContextCompat.getColor(MainActivity.this, R.color.tab_color_true);
                        completeColor = ContextCompat.getColor(MainActivity.this, R.color.tab_color_true);
                    }

                    if (selectImageType) {
                        checkedBoxDrawable = R.drawable.select_cb;
                    } else {
                        checkedBoxDrawable = 0;
                    }

                    /*
                    * 实体类进行多元素设置
                    * */
                    FunctionOptions options = new FunctionOptions.Builder()
                            .setType(selectType) // 图片or视频 FunctionConfig.TYPE_IMAGE  TYPE_VIDEO
                            .setCropMode(copyMode) // 裁剪模式 默认、1:1、3:4、3:2、16:9
                            .setCompress(isCompress) //是否压缩
                            .setEnablePixelCompress(true) //是否启用像素压缩
                            .setEnableQualityCompress(true) //是否启质量压缩
                            .setMaxSelectNum(maxSelectNum) // 可选择图片的数量
                            .setSelectMode(selectMode) // 单选 or 多选
                            .setShowCamera(isShow) //是否显示拍照选项 这里自动根据type 启动拍照或录视频
                            .setEnablePreview(enablePreview) // 是否打开预览选项
                            .setEnableCrop(enableCrop) // 是否打开剪切选项
                            .setPreviewVideo(isPreviewVideo) // 是否预览视频(播放) mode or 多选有效
                            .setCheckedBoxDrawable(checkedBoxDrawable)
                            .setRecordVideoDefinition(FunctionConfig.HIGH) // 视频清晰度
                            .setRecordVideoSecond(60) // 视频秒数
                            .setGif(false)// 是否显示gif图片，默认不显示
                            .setCropW(cropW) // cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                            .setCropH(cropH) // cropH-->裁剪高度 值不能小于100 如果值大于图片原始宽高 将返回原图大小
                            .setMaxB(maxB) // 压缩最大值 例如:200kb  就设置202400，202400 / 1024 = 200kb
                            .setPreviewColor(previewColor) //预览字体颜色
                            .setCompleteColor(completeColor) //已完成字体颜色
                            .setPreviewBottomBgColor(previewBottomBgColor) //预览底部背景色
                            .setBottomBgColor(bottomBgColor) //图片列表底部背景色
                            .setGrade(Luban.THIRD_GEAR) // 压缩档次 默认三档
                            .setCheckNumMode(isCheckNumMode)//可选数量
                            .setCompressQuality(100) // 图片裁剪质量,默认无损
                            .setImageSpanCount(4) // 每行个数
                            .setSelectMedia(selectMedia) // 已选图片，传入在次进去可选中，不能传入网络图片
                            .setCompressFlag(compressFlag) // 1 系统自带压缩 2 luban压缩
                            .setCompressW(compressW) // 压缩宽 如果值大于图片原始宽高无效
                            .setCompressH(compressH) // 压缩高 如果值大于图片原始宽高无效
                            .setThemeStyle(themeStyle) // 设置主题样式
                            .create();

                    // 先初始化参数配置，在启动相册
                    PictureConfig.getPictureConfig().init(options).openPhoto(mContext, resultCallback);
                    // 只拍照
                    //PictureConfig.getPictureConfig().init(options).startOpenCamera(mContext, resultCallback);
                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", selectMedia.size() + "");
            LocalMedia media = resultList.get(0);
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                String path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                String path = media.getCompressPath();
            } else {
                // 原图地址
                String path = media.getPath();
            }
            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            //普通模式
            case R.id.rb_ordinary:
                isCheckNumMode = false;
                break;
            //qq模式
            case R.id.rb_qq:
                isCheckNumMode = true;
                break;
            //单选
            case R.id.rb_single:
                selectMode = FunctionConfig.MODE_SINGLE;
                break;
            //多选
            case R.id.rb_multiple:
                selectMode = FunctionConfig.MODE_MULTIPLE;
                break;
            //获取图片
            case R.id.rb_image:
                selectType = LocalMediaLoader.TYPE_IMAGE;
                break;
            //获取视频
            case R.id.rb_video:
                selectType = LocalMediaLoader.TYPE_VIDEO;
                break;
            //显示拍摄
            case R.id.rb_photo_display:
                isShow = true;
                break;
            //隐藏拍摄
            case R.id.rb_photo_hide:
                isShow = false;
                break;
            //比例设置
            case R.id.rb_default:
                copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
                break;
            case R.id.rb_to1_1:
                copyMode = FunctionConfig.CROP_MODEL_1_1;
                break;
            case R.id.rb_to3_2:
                copyMode = FunctionConfig.CROP_MODEL_3_2;
                break;
            case R.id.rb_to3_4:
                copyMode = FunctionConfig.CROP_MODEL_3_4;
                break;
            case R.id.rb_to16_9:
                copyMode = FunctionConfig.CROP_MODEL_16_9;
                break;
            //图片预览开启
            case R.id.rb_preview:
                enablePreview = true;
                break;
            //图片预览关闭
            case R.id.rb_preview_false:
                enablePreview = false;
                break;
            //视频预览开启
            case R.id.rb_preview_video:
                isPreviewVideo = true;
                break;
            //视频预览关闭
            case R.id.rb_preview_video_false:
                isPreviewVideo = false;
                break;
            //允许裁剪
            case R.id.rb_yes_copy:
                enableCrop = true;
                break;
            //禁止裁剪
            case R.id.rb_no_copy:
                enableCrop = false;
                break;
            //默认主题
            case R.id.rb_theme1:
                theme = false;
                break;
            //蓝色主题
            case R.id.rb_theme2:
                theme = true;
                break;
            //默认图片选择样式
            case R.id.rb_select1:
                selectImageType = false;
                break;
            //自定义图片选择样式
            case R.id.rb_select2:
                selectImageType = true;
                break;
            //默认不压缩图片
            case R.id.rb_compress_false:
                isCompress = false;
                rgbs10.setVisibility(View.GONE);
                et_kb.setVisibility(View.GONE);
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            //压缩图片
            case R.id.rb_compress_true:
                isCompress = true;
                et_kb.setVisibility(View.VISIBLE);
                if (compressFlag == 2) {
                    ll_luban_wh.setVisibility(View.VISIBLE);
                }
                rgbs10.setVisibility(View.VISIBLE);
                break;
            //系统自带压缩
            case R.id.rb_system:
                compressFlag = 1;
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            //鲁班压缩
            case R.id.rb_luban:
                compressFlag = 2;
                ll_luban_wh.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 判断 一个字段的值否为空
     * equalsIgnoreCase()忽略大小写的比较方法
     *
     * @param s
     * @return
     * @author Michael.Zhang 2013-9-7 下午4:39:00
     */

    public boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }

        return false;
    }

}
