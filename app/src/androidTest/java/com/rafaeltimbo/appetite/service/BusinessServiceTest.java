package com.rafaeltimbo.appetite.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.rafaeltimbo.appetite.broadcast.BusinessListReadyReceiver;
import com.rafaeltimbo.appetite.model.Business;
import com.rafaeltimbo.appetite.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class BusinessServiceTest {

    private BusinessServiceClient businessServiceClient;
    private BusinessListReadyReceiver businessListReadyReceiver;
    private SharedPreferencesService sp;
    private Context context;
    private String TAG = "Appetite.BusinessServiceTest";

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getTargetContext();
        businessServiceClient = new BusinessServiceClient(context);
        sp = new SharedPreferencesService(context);

    }

    @After
    public void tearDown() throws Exception {
        businessServiceClient.destroy();
    }


    @Test
    public void test_onReceive() throws Exception {
        BusinessListReadyReceiver businessListReadyReceiver;
        final Object syncObject = new Object();

        businessListReadyReceiver = new BusinessListReadyReceiver(new BusinessListReadyReceiver.OnReceive() {

            @Override
            public void onReceive(ArrayList<Business> businessList) {

                assertTrue("businessList returned empty",businessList.size() > 0);
                synchronized(syncObject) {
                    syncObject.notify();
                }
            }
        });
        this.context.registerReceiver(businessListReadyReceiver, businessListReadyReceiver.getIntentFilter());
        businessServiceClient.refreshBusinessList();

        synchronized (syncObject) {
            syncObject.wait();
            this.context.unregisterReceiver(businessListReadyReceiver);
        }
    }

    @Test
    public void test_spRange() throws Exception {

        BusinessListReadyReceiver businessListReadyReceiver;
        final Object syncObject = new Object();

        businessListReadyReceiver = new BusinessListReadyReceiver(new BusinessListReadyReceiver.OnReceive() {
            int numberOfBusiness1 = -1;
            int numberOfBusiness2 = -1;

            @Override
            public void onReceive(ArrayList<Business> businessList) {
                assertTrue("businessList is empty!", businessList.size() > 0);
                if (numberOfBusiness1 < 0) {
                    // returns 20 because of yelp limit parameter (pagination)
                    numberOfBusiness1 = businessList.size();
                    System.out.println(TAG + " b1: " + numberOfBusiness1);
                } else {
                    numberOfBusiness2 = businessList.size();
                    System.out.println(TAG + " b2: " + numberOfBusiness2);
                    //assertNotEquals(numberOfBusiness1, numberOfBusiness2);
                    synchronized(syncObject) {
                        syncObject.notify();
                    }
                }
            }
        });
        this.context.registerReceiver(businessListReadyReceiver, businessListReadyReceiver.getIntentFilter());

        System.out.println(TAG + "test_spRange:");
        sp.saveToSharedPreferences(Constants.SHARED_PREFERENCES_SEARCH_RANGE, "1");
        businessServiceClient.refreshBusinessList();

        sp.saveToSharedPreferences(Constants.SHARED_PREFERENCES_SEARCH_RANGE, "40");
        businessServiceClient.refreshBusinessList();

        synchronized (syncObject) {
            syncObject.wait();
            this.context.unregisterReceiver(businessListReadyReceiver);
        }
    }

}