/**
 * 
 */

function removeParametersFromUrl() {
    if (window.location.search != '') {
        window.history.pushState("", "", window.location.href.replace(window.location.search, ''));
    }
}

function emHeight() {
    return $('#top').outerHeight(true) - $('#top').outerHeight();
}

function adjustFooterPosition() {
    var footerWidth = $('.footer-message').outerWidth(true);
    $('.footer-message').css({"left":(window.innerWidth-footerWidth)/2});
}

function startDownload() {
    PF('statusDialog').show();
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
 * @param globalIndex the global index of the entity
 * @param tableVarName the name of the PrimeFaces table variable
 */
function selectEntityInTable(globalIndex, tableVarName) {
    // selectEntityInTable() only works on page load, so it can be used for navigating to
    //     an entity form another screen.
    var tableWidget = PF(tableVarName);

    if (tableWidget.cfg.scrollLimit < 1 || tableWidget.cfg.scrollLimit < globalIndex) return;

    if (tableWidget.scrollOffset < globalIndex) {
        tableWidget.loadLiveRows();
        setTimeout(function(){ selectEntityInTable(globalIndex, tableVarName); }, 50);
        return;
    }
    tableWidget.selectRow(globalIndex);
    scrollSelectedIntoView(tableWidget);
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
