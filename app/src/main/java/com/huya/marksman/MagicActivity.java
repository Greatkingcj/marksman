package com.huya.marksman;

import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huya.marksman.commonanimator.Direction;
import com.huya.marksman.magicsurfaceview.MagicMultiSurface;
import com.huya.marksman.magicsurfaceview.MagicMultiSurfaceUpdater;
import com.huya.marksman.magicsurfaceview.MagicSurface;
import com.huya.marksman.magicsurfaceview.MagicSurfaceMatrixUpdater;
import com.huya.marksman.magicsurfaceview.MagicSurfaceModelUpdater;
import com.huya.marksman.magicsurfaceview.MagicSurfaceView;
import com.huya.marksman.magicsurfaceview.MagicUpdater;
import com.huya.marksman.magicsurfaceview.MagicUpdaterListener;
import com.huya.marksman.updater.WaveAnimUpdater;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author charles
 */
public class MagicActivity extends AppCompatActivity {

    @Bind(R.id.page_view_container) View pageViewContainer;
    @Bind(R.id.page_surface_view) MagicSurfaceView pageSurfaceView;
    @Bind(R.id.page_title_bar) View pageTitleBar;
    @Bind(R.id.fl_page_content) FrameLayout flPageContent;
    @Bind(R.id.tv_page_title) TextView tvPageTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_magic);
        ButterKnife.bind(this);

        if (!show()) {
            pageViewContainer.setVisibility(View.VISIBLE);
        }
    }


    private boolean show() {
        MagicUpdater updater = getPageUpdater(false);
        if (updater != null) {
            return showWithSurface(updater);
        }
        MagicMultiSurfaceUpdater multiUpdater = getPageMultiUpdater(false);
        if (multiUpdater != null) {
            return showWithMultiSurface(multiUpdater);
        }
        return false;
    }

    private boolean showWithSurface(MagicUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                pageViewContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStop() {
                pageViewContainer.setVisibility(View.VISIBLE);
                pageSurfaceView.setVisibility(View.GONE);
                pageSurfaceView.release();
                onPageAnimEnd();
            }
        });
        final MagicSurface s = new MagicSurface(pageViewContainer)
                .setGrid(pageAnimRowCount(), pageAnimColCount())
                .drawGrid(false);
        if (updater instanceof MagicSurfaceMatrixUpdater) {
            s.setMatrixUpdater((MagicSurfaceMatrixUpdater)updater);
        } else {
            s.setModelUpdater(((MagicSurfaceModelUpdater) updater));
        }
        pageSurfaceView.setVisibility(View.VISIBLE);
        pageSurfaceView.render(s);
        return true;
    }

    private boolean showWithMultiSurface(MagicMultiSurfaceUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                pageViewContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStop() {
                pageViewContainer.setVisibility(View.VISIBLE);
                pageSurfaceView.setVisibility(View.GONE);
                pageSurfaceView.release();
                onPageAnimEnd();
            }
        });
        final MagicMultiSurface s = new MagicMultiSurface(pageViewContainer, pageAnimRowCount(), pageAnimColCount());
        s.setUpdater(updater);
        pageSurfaceView.setVisibility(View.VISIBLE);
        pageSurfaceView.render(s);
        return true;
    }

    private boolean hide() {
        MagicUpdater updater = getPageUpdater(true);
        if (updater != null) {
            return hideWithSurface(updater);
        }

        MagicMultiSurfaceUpdater multiUpdater = getPageMultiUpdater(true);
        if (multiUpdater != null) {
            return hideWithMultiSurface(multiUpdater);
        }
        return false;
    }

    private boolean hideWithSurface(MagicUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                pageViewContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStop() {
                pageSurfaceView.setVisibility(View.GONE);
                pageSurfaceView.release();
                finish();
            }
        });
        MagicSurface s = new MagicSurface(pageViewContainer)
                .setGrid(pageAnimRowCount(), pageAnimColCount())
                .drawGrid(false);
        if (updater instanceof MagicSurfaceMatrixUpdater) {
            s.setMatrixUpdater((MagicSurfaceMatrixUpdater) updater);
        } else {
            s.setModelUpdater((MagicSurfaceModelUpdater) updater);
        }
        pageSurfaceView.setVisibility(View.VISIBLE);
        pageSurfaceView.render(s);
        return true;
    }

    private boolean hideWithMultiSurface(MagicMultiSurfaceUpdater updater) {
        updater.addListener(new MagicUpdaterListener() {
            @Override
            public void onStart() {
                pageViewContainer.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onStop() {
                pageSurfaceView.setVisibility(View.GONE);
                // 动画完成释放资源
                pageSurfaceView.release();
                finish();
            }
        });
        final MagicMultiSurface s = new MagicMultiSurface(pageViewContainer, pageAnimRowCount(), pageAnimColCount());
        s.setUpdater(updater);
        pageSurfaceView.setVisibility(View.VISIBLE);
        pageSurfaceView.render(s);
        return true;
    }

    protected MagicUpdater getPageUpdater(boolean isHide) {
        if (isHide) {
            return new WaveAnimUpdater(true, Direction.RIGHT, false);
        } else {
            return new WaveAnimUpdater(false, Direction.RIGHT, false);
        }
    }

    protected MagicMultiSurfaceUpdater getPageMultiUpdater(boolean isHide) {
        return null;
    }

    @Override
    public void setTitle(CharSequence title) {
        tvPageTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        tvPageTitle.setText(titleId);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, flPageContent, true);
    }

    protected MagicSurfaceView getPageSurfaceView() {
        return pageSurfaceView;
    }

    protected View getPageViewContainer() {
        return pageViewContainer;
    }

    protected void showPage() {
        pageViewContainer.setVisibility(View.VISIBLE);
    }

    protected void hidePage() {
        pageViewContainer.setVisibility(View.INVISIBLE);
    }

    protected void hidePageTitleBar() {
        pageTitleBar.setVisibility(View.GONE);
    }

    protected void setPageBackground(@ColorRes int colorResource) {
        pageViewContainer.setBackgroundColor(ContextCompat.getColor(this, colorResource));
    }

    protected void setPageContentBg(@ColorRes int colorResource) {
        flPageContent.setBackgroundResource(colorResource);
    }

    /**
     * 页面转场动画对应 SurfaceModel 行数
     * @return
     */
    protected int pageAnimRowCount() {
        return 30;
    }

    /**
     * 页面转场动画对应 SurfaceModel 列数
     * @return
     */
    protected int pageAnimColCount() {
        return 30;
    }

    /**
     * 页面转场动画入场动画完成后调用
     * @return
     */
    protected void onPageAnimEnd() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageSurfaceView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!hide()) {
            finish();
        }
    }
}
