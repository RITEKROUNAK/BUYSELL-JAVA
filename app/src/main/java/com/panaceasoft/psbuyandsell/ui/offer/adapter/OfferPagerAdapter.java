package com.panaceasoft.psbuyandsell.ui.offer.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.panaceasoft.psbuyandsell.ui.offer.OfferBuyerFragment;
import com.panaceasoft.psbuyandsell.ui.offer.OfferSellerFragment;

public class OfferPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    public OfferSellerFragment offersellerFragmentList;
    public OfferBuyerFragment offerbuyerFragmentList;

    public OfferPagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                offersellerFragmentList = new OfferSellerFragment();
                return offersellerFragmentList;

            case 1:
                offerbuyerFragmentList = new OfferBuyerFragment();
                return offerbuyerFragmentList;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
