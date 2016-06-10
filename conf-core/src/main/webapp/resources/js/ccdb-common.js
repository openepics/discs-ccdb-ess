/**
 * 
 */

/** Global CCDB environment */
var CCDB = {
		"config" : { 
			"jumpToElementOnLoad" : false
		},
		"jumpToElementOnLoadHandler" : function(xhr, target) {
			selectEntityInTable(CCDB.dataLoaderInternal.dataKey, CCDB.dataLoaderInternal.tableVarName);
        },
        "dataLoaderInternal" : {},
        "oldABHandler" : null,
        "disabledAjaxButton" : null,
        "newABHandler" : function(a, c) {
        	if (a.s) {
        		var sourceId = PrimeFaces.escapeClientId(a.s);
        		var widget = $(sourceId);
        		if (widget.hasClass("ui-button") && (CCDB.disabledAjaxButton == null)) {
        			CCDB.disabledAjaxButton = widget;
        			widget.removeClass('ui-state-hover ui-state-focus ui-state-active').addClass('ui-state-disabled')
        				.attr('disabled', 'disabled');        			
                	jQuery(document).on("pfAjaxComplete", CCDB.ajaxButtonHandler);
        		}
        	}
    		CCDB.oldABHandler.call(PrimeFaces, a, c);
        },
        "takeOverAb" : function() {
        	if (!CCDB.oldABHandler) {
        		CCDB.oldABHandler = PrimeFaces.ab;
        		PrimeFaces.ab = CCDB.newABHandler;
        	}
        },
        "ajaxButtonHandler" : function(xhr, target, errorThrown) {
        	CCDB.disabledAjaxButton.removeClass('ui-state-disabled').removeAttr('disabled');
        	CCDB.disabledAjaxButton = null;
        	jQuery(document).off("pfAjaxComplete", CCDB.ajaxButtonHandler);
        }
}


function removeParametersFromUrl() {
    if (window.location.search != "") {
        window.history.pushState("", "", window.location.href.replace(window.location.search, ""));
    }
}

function emHeight() {
    return $("#top").outerHeight(true) - $("#top").outerHeight();
}

function adjustFooterPosition() {
    var footerWidth = $(".footer-message").outerWidth(true);
    $(".footer-message").css({"left":(window.innerWidth-footerWidth)/2});
}

function startDownload() {
    PF("statusDialog").show();
}

function scrollSelectedIntoView(tableWidget) {
    var selectionTable;
    if (tableWidget.selection === undefined) {
        selectionTable = tableWidget.selections;
    } else {
        selectionTable = tableWidget.selection;
    }
    if (selectionTable.length > 0) {
        var scrollableBodyHeight = tableWidget.scrollBody[0].clientHeight;
        var selectedNodeSelector = "tr[data-rk='" + selectionTable[0] + "']";
        var selectedNode = $(selectedNodeSelector);
        var selectedNodePosition = selectedNode[0].offsetTop;
        var lowerLimit = Math.max(0, selectedNodePosition - scrollableBodyHeight + selectedNode.outerHeight());
        if (tableWidget.scrollBody.scrollTop() < lowerLimit || tableWidget.scrollBody.scrollTop() > selectedNodePosition) {
            tableWidget.scrollBody.scrollTop(Math.max(0,selectedNodePosition-scrollableBodyHeight/2));
        }
    }
}

/**
 * selectEntityInTable() only works on page load, so it can be used for navigating to an entity form another screen. 
 * @param dataKey the global index of the entity
 * @param tableVarName the name of the PrimeFaces table variable
 */
function selectEntityInTable(dataKey, tableVarName) {
    // selectEntityInTable() only works on page load, so it can be used for navigating to
    //     an entity form another screen.
	bootstrapDataLoader(dataKey, tableVarName);
    var tableWidget = PF(tableVarName);
    // search for entity
    var lineRef = $(tableWidget.jqId + " tr[data-rk='" + dataKey + "']");
    
    if (lineRef.length > 0) {
    	// entity found
    	jQuery(document).off("pfAjaxComplete", CCDB.jumpToElementOnLoadHandler);
    	var rowNumber = Number(lineRef[0].getAttribute("data-ri"));
        tableWidget.selectRow(rowNumber);
        scrollSelectedIntoView(tableWidget);
    } else if (!tableWidget.allLoadedLiveScroll) {
    	// not found, but there's more data
        tableWidget.loadLiveRows();
    } else {
    	// entity not found
    	jQuery(document).off("pfAjaxComplete", CCDB.jumpToElementOnLoadHandler);
    }
}

function bootstrapDataLoader(dataKey, tableVarName) {
	if (CCDB.config.jumpToElementOnLoad) {
		CCDB.config.jumpToElementOnLoad = false;
		// store the parameters
		CCDB.dataLoaderInternal = {
				"dataKey" : dataKey,
				"tableVarName" : tableVarName
		}
		jQuery(document).on("pfAjaxComplete", CCDB.jumpToElementOnLoadHandler);
	}
}

function resizeDeleteList(deleteDialogId) {
	var deleteTable = $("#" + deleteDialogId + " .dialogListTable .ui-datatable-scrollable-body");
	var tableContentHeight = $("#" + deleteDialogId + " .dialogListTable .ui-datatable-data").outerHeight(true);
	var calculatedHeight = emHeight() * 13;
	if (tableContentHeight >= emHeight() * 25) {
		calculatedHeight = emHeight() * 25;
	} else if (tableContentHeight >= emHeight() * 13) {
		calculatedHeight = tableContentHeight + 2;
	}
	deleteTable.css({"height":calculatedHeight});
}

