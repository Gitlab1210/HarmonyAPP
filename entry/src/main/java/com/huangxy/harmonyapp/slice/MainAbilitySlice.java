package com.huangxy.harmonyapp.slice;

import com.huangxy.harmonyapp.ResourceTable;
import com.huangxy.harmonyapp.datamodel.BottomBarItemInfo;
import com.huangxy.harmonyapp.datamodel.BottomBarViewModel;
import com.huangxy.harmonyapp.datamodel.ListItemInfo;
import com.huangxy.harmonyapp.datamodel.ListViewModel;
import com.huangxy.harmonyapp.utils.ListItemProvider;

import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.Image;
import ohos.agp.components.ListContainer;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import ohos.agp.window.dialog.ToastDialog;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Build the whole list page.
 */
public class MainAbilitySlice extends AbilitySlice {
    private static final int OVER_SCROLL_PERCENT = 40;

    private static final float OVER_SCROLL_RATE = 0.6f;

    private static final int REMAIN_VISIBLE_PERCENT = 20;

    private static final int DUR = 500;

    private static final int OFFSET_X = 0;

    private static final int OFFSET_Y = 50;
    private final int[] bottomItemLayoutIds = {ResourceTable.Id_bottom_item_layout1,
            ResourceTable.Id_bottom_item_layout2, ResourceTable.Id_bottom_item_layout3,
            ResourceTable.Id_bottom_item_layout4, ResourceTable.Id_bottom_item_layout5};
    private AbilitySlice abilitySlice;
    private ListItemProvider listItemProvider;
    private ListContainer listContainer;
    private List<BottomBarItemInfo> bottomBarItemInfos;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        abilitySlice = this;

        super.setUIContent(ResourceTable.Layout_list_layout);

        // Set app bar.
        setAppBar();

        // Set recycle view.
        setRecycleView();

        // Set bottom tool bar.
        setBottomToolBar();
    }

    private void setAppBar() {
        Component.ClickedListener titleButtonClickListener = component -> {
            ToastDialog toastDialog = new ToastDialog(abilitySlice);
            String componentDescription = component.getComponentDescription().toString();
            toastDialog.setContentText("You clicked " + componentDescription + ".")
                    .setDuration(DUR)
                    .setOffset(OFFSET_X, OFFSET_Y)
                    .show();
        };

        DirectionalLayout firstItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_first_item_layout);
        firstItemLayout.setClickedListener(titleButtonClickListener);
        DirectionalLayout secondItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_second_item_layout);
        secondItemLayout.setClickedListener(titleButtonClickListener);
        DirectionalLayout thirdItemLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_third_item_layout);
        thirdItemLayout.setClickedListener(titleButtonClickListener);
        Text titleText = (Text) findComponentById(ResourceTable.Id_title);
        titleText.setClickedListener(titleButtonClickListener);
    }

    /**
     * Set list item click event.
     */
    private void setListClickListener() {
        ListContainer.ItemClickedListener itemClickedListener = (container, component, position, id) -> {
            ListItemInfo listItemInfo = (ListItemInfo) listItemProvider.getItem(position);
            ToastDialog toastDialog = new ToastDialog(abilitySlice);
            toastDialog.setContentText("You clicked " + listItemInfo.getTextMain() + ".")
                    .setDuration(DUR)
                    .setOffset(OFFSET_X, OFFSET_Y)
                    .show();
        };

        listContainer.setItemClickedListener(itemClickedListener);
    }

    private void setListReboundAnimation() {
        listContainer.setReboundEffect(true);
        listContainer.setReboundEffectParams(OVER_SCROLL_PERCENT, OVER_SCROLL_RATE, REMAIN_VISIBLE_PERCENT);
    }

    private void setRecycleView() {
        listContainer = (ListContainer) abilitySlice.findComponentById(ResourceTable.Id_list_container);
        if (listContainer != null) {
            ListViewModel listViewModel = new ListViewModel();
            List<ListItemInfo> listItemInfos = listViewModel.getDataItemList();
            listItemProvider = new ListItemProvider(listItemInfos);
            listContainer.setItemProvider(listItemProvider);
            // Set list click event.
            setListClickListener();
            setListReboundAnimation();
        }
    }

    private void unselected() {
        for (int bottomItemLayoutId : bottomItemLayoutIds) {
            DirectionalLayout bottomItemLayout = (DirectionalLayout) abilitySlice.findComponentById(bottomItemLayoutId);
            String componentName = bottomItemLayout.getName();
            // Length minus 1 is the last character index, and '1' is the first bottom button index.
            int position = componentName.charAt(componentName.length() - 1) - '1';
            // To deal with the case of that bottom item count smaller than allowable maximum item count.
            if (position < bottomBarItemInfos.size()) {
                Image image = (Image) bottomItemLayout.findComponentById(ResourceTable.Id_bottom_item_image);
                Text text = (Text) bottomItemLayout.findComponentById(ResourceTable.Id_bottom_item_text);
                image.setPixelMap(bottomBarItemInfos.get(position).getBarOriImageId());
                text.setTextColor(Color.BLACK);
            }
        }
    }

    private void setBottomToolBar() {
        // Set bottom item click event.
        Component.ClickedListener bottomButtonClickedListener = component -> {
            unselected();
            String componentName = component.getName();
            // Length minus 1 is the last character index, and '1' is the first bottom button index.
            int position = componentName.charAt(componentName.length() - 1) - '1';
            // To deal with the case of that bottom item count smaller than allowable maximum item count.
            if (position < bottomBarItemInfos.size()) {
                Image image = (Image) component.findComponentById(ResourceTable.Id_bottom_item_image);
                Text text = (Text) component.findComponentById(ResourceTable.Id_bottom_item_text);
                image.setImageAndDecodeBounds(bottomBarItemInfos.get(position).getBarActivatedImageId());
                text.setTextColor(Color.BLUE);
            }
        };

        // Set bottom tool bar.
        BottomBarViewModel bottomBarViewModel = new BottomBarViewModel();
        bottomBarItemInfos = bottomBarViewModel.getBarItemList();
        IntStream.range(0, bottomBarItemInfos.size()).forEach(position -> {
            DirectionalLayout bottomItemLayout =
                    (DirectionalLayout) abilitySlice.findComponentById(bottomItemLayoutIds[position]);
            bottomItemLayout.setVisibility(Component.VISIBLE);
            Image image = (Image) bottomItemLayout.findComponentById(ResourceTable.Id_bottom_item_image);
            Text text = (Text) bottomItemLayout.findComponentById(ResourceTable.Id_bottom_item_text);
            if (position == 0) {
                image.setImageAndDecodeBounds(bottomBarItemInfos.get(position).getBarActivatedImageId());
                text.setTextColor(Color.BLUE);
            } else {
                image.setImageAndDecodeBounds(bottomBarItemInfos.get(position).getBarOriImageId());
            }
            text.setText(bottomBarItemInfos.get(position).getBarText());

            bottomItemLayout.setClickedListener(bottomButtonClickedListener);
        });
    }

    @Override
    protected void onActive() {
        super.onActive();
    }

    @Override
    protected void onBackground() {
        super.onBackground();
    }
}
