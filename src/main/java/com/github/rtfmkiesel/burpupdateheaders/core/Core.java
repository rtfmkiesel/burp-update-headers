package com.github.rtfmkiesel.burpupdateheaders.core;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.github.rtfmkiesel.burpupdateheaders.contextmenu.ContextMenuItemUpdateByHost;

public class Core implements BurpExtension {
    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName("Update Headers");
        api.userInterface().registerContextMenuItemsProvider(new ContextMenuItemUpdateByHost(api));
    }
}