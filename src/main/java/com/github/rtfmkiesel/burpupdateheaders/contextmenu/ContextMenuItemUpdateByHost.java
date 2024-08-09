package com.github.rtfmkiesel.burpupdateheaders.contextmenu;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.logging.Logging;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import static java.util.Collections.emptyList;

public class ContextMenuItemUpdateByHost implements ContextMenuItemsProvider {
    private final MontoyaApi api;
    private final Logging logging;

    public ContextMenuItemUpdateByHost(MontoyaApi api) {
        this.api = api;
        this.logging = api.logging();
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.REPEATER) || event.isFromTool(ToolType.INTRUDER))
        {
            //this.logging.logToOutput(String.format("Invoked from tool %s", event.toolType()));

            if (event.messageEditorRequestResponse().isPresent())
            {
                //this.logging.logToOutput("RequestResponse object is present");

                // Get the selected RequestResponse
                MessageEditorHttpRequestResponse selectedHttpRequestResponse = event.messageEditorRequestResponse().orElse(null);
                if (selectedHttpRequestResponse != null) {
                    //this.logging.logToOutput(String.format("Target: %s", selectedHttpRequestResponse.requestResponse().request().header("Host").value()));

                    // Get all current headers
                    List<HttpHeader> currentHeaders = selectedHttpRequestResponse.requestResponse().request().headers();
                    if (currentHeaders != null) {
                        List<Component> menuItems = new ArrayList<>();
                        //this.logging.logToOutput(String.format("Target has %d headers", currentHeaders.size()));

                        // Get the host header as a filter for the history
                        HttpHeader currentHostHeader = selectedHttpRequestResponse.requestResponse().request().header("Host");
                        //this.logging.logToOutput(String.format("Filtering for history items with Host header %s", currentHostHeader.value()));

                        // Filter history by current host header
                        List<ProxyHttpRequestResponse> filteredHistory = api.proxy().history(requestResponse -> Objects.equals(requestResponse.request().header("Host"), currentHostHeader));
                        ListIterator<ProxyHttpRequestResponse> historyIterator = filteredHistory.listIterator(filteredHistory.size());
                        //this.logging.logToOutput(String.format("Found %s history items with matching Host header", filteredHistory.size()));

                        // Add a menu item for each header in the current headers
                        currentHeaders.forEach(selectedHttpHeader -> {
                            String headerName = selectedHttpHeader.name();
                            JMenuItem menuItemHeader = new JMenuItem(headerName);
                            //this.logging.logToOutput(String.format("Added %s to the context menu", headerName));
                            menuItemHeader.addActionListener(l -> {
                                //this.logging.logToOutput(String.format("User want to update header %s", headerName));

                                // For each entry in the filtered history (reverse via previous() to start at the latest entry)
                                while (historyIterator.hasPrevious()) {
                                    ProxyHttpRequestResponse entry = historyIterator.previous();

                                    // Get the requested header
                                    HttpHeader newestHeader = entry.request().header(headerName);

                                    if (newestHeader != null) {
                                        //this.logging.logToOutput(String.format("Found new header in request %s made at %s", entry.request().path(), entry.time()));

                                        // Update the request with the newest header
                                        selectedHttpRequestResponse.setRequest(selectedHttpRequestResponse.requestResponse().request().withHeader(newestHeader));
                                        //this.logging.logToOutput(String.format("Updated header %s", headerName));

                                        // Only on first match
                                        break;
                                    }
                                }
                            });

                            menuItems.add(menuItemHeader);
                        });

                        return menuItems;
                    }
                }
            }
        }

        return emptyList();
    }
}