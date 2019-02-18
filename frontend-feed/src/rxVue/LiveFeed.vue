<template>
    <div style="height: 100%">
                <ag-grid-vue style="width: 100%; height: 100%;" class="ag-theme-balham" id="myGrid"
                        :gridOptions="gridOptions"
                        @grid-ready="onGridReady"
                        :columnDefs="columnDefs"
                        :defaultColDef="defaultColDef"
                        :debug="true"
                        :rowSelection="multiple"
                        :rowModelType="rowModelType"
                        :getRowNodeId="getRowNodeId"
                        :components="components"
                        :rowData="rowData">
                </ag-grid-vue>
    </div>
</template>

<script>
    import Vue from "vue";      
    import { AgGridVue } from "ag-grid-vue";

    import axios from 'axios'
    
    export default {
        components: {
            "ag-grid-vue": AgGridVue
        },
        data: function() {
            return {
                gridOptions: null,
                gridApi: null,
                columnApi: null,
                columnDefs: null,
                defaultColDef: null,
                rowSelection: null,
                rowModelType: null,
                getRowNodeId: null,
                components: null,
                rowData: null,
            };
        },
        beforeMount() {
            this.gridOptions = {};
            this.columnDefs = [
            {
                headerName: "#",
                width: 50,
                cellRenderer: "rowIdRenderer"
            },
            {
                headerName: "Code",
                field: "code",
                width: 70
            },
            {
                headerName: "Name",
                field: "name",
                width: 300
            },
            {
                headerName: "Bid",
                field: "bid",
                width: 100,
                cellClass: "cell-number",
                valueFormatter: numberFormatter,
                cellRenderer: "agAnimateShowChangeCellRenderer"
            },
            {
                headerName: "Mid",
                field: "mid",
                width: 100,
                cellClass: "cell-number",
                valueFormatter: numberFormatter,
                cellRenderer: "agAnimateShowChangeCellRenderer"
            },
            {
                headerName: "Ask",
                field: "ask",
                width: 100,
                cellClass: "cell-number",
                valueFormatter: numberFormatter,
                cellRenderer: "agAnimateShowChangeCellRenderer"
            },
            {
                headerName: "Volume",
                field: "volume",
                width: 80,
                cellClass: "cell-number",
                cellRenderer: "agAnimateSlideCellRenderer"
            }
            ];
            this.defaultColDef = { resizable: true };
            this.rowSelection = "multiple";
            this.getRowNodeId = data => {
                return data.code;
            };
            this.components = {
                rowIdRenderer: params => {
                    return "" + params.rowIndex;
                }
            };
            const httpRequest = new XMLHttpRequest();

            httpRequest.open("GET", "https://rawgit.com/ag-grid/ag-grid/master/packages/ag-grid-docs/src/stocks.json");
            httpRequest.send();
            httpRequest.onreadystatechange = () => {
                if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                    const rowDataTmp = [];
                    JSON.parse(httpRequest.responseText)
                        .forEach(function(item) {
                            rowDataTmp.push({
                                code: item.code,
                                name: item.name,
                                bid:  item.bid != undefined ? item.bid : '',
                                mid: item.mid != undefined ? item.mid : '',
                                ask: item.ask != undefined ? item.ask : '',
                                volume: item.volume != undefined ? item.volume : ''                           
                        }); 
                    });
                    this.rowData = rowDataTmp;
                }
            };
        },
        mounted() {
            this.gridApi = this.gridOptions.api;
            this.gridColumnApi = this.gridOptions.columnApi;
        },
        methods: {
            onGridReady(params) {
                const httpRequest = new XMLHttpRequest();

                httpRequest.open("GET", "https://rawgit.com/ag-grid/ag-grid/master/packages/ag-grid-docs/src/stocks.json");
                httpRequest.send();
                httpRequest.onreadystatechange = () => {
                    if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                        const rowDataTmp = [];
                        JSON.parse(httpRequest.responseText)
                            .forEach(function(item) {
                                rowDataTmp.push({
                                    code: item.code,
                                    name: item.name,
                                    bid:  item.bid != undefined ? item.bid : '',
                                    mid: item.mid != undefined ? item.mid : '',
                                    ask: item.ask != undefined ? item.ask : '',
                                    volume: item.volume != undefined ? item.volume : ''                           
                            }); 
                        });
                        this.rowData = rowDataTmp;
                    }
                };
            }
        }
    };

    function numberFormatter(params) {
        if (typeof params.value === "number") {
            return params.value.toFixed(2);
        } else {
            return params.value;
        }
    }

    function createMockServer() {
        function MockServer() {
            this.connections = {};
            this.nextConnectionId = 0;
            setInterval(this.periodicallyUpdateData.bind(this), 100);
        }
        MockServer.prototype.periodicallyUpdateData = function() {
            var changes = [];
            this.makeSomePriceChanges(changes);
            this.makeSomeVolumeChanges(changes);
            this.informConnectionsOfChanges(changes);
        };
        MockServer.prototype.informConnectionsOfChanges = function(changes) {
            var that = this;
            Object.keys(this.connections).forEach(function(connectionId) {
            var connection = that.connections[connectionId];
            var changesThisConnection = [];
            changes.forEach(function(change) {
                var changeInRange = change.rowIndex >= connection.firstRow && change.rowIndex <= connection.lastRow;
                if (changeInRange) {
                changesThisConnection.push(change);
                }
            });
            if (changesThisConnection.length > 0) {
                that.sendEventAsync(connectionId, {
                eventType: "dataUpdated",
                changes: changesThisConnection
                });
            }
            });
        };
        MockServer.prototype.makeSomeVolumeChanges = function(changes) {
            for (var i = 0; i < 10; i++) {
            var index = Math.floor(this.allData.length * Math.random());
            var dataItem = this.allData[index];
            var move = Math.floor(10 * Math.random()) - 5;
            var newValue = dataItem.volume + move;
            dataItem.volume = newValue;
            changes.push({
                rowIndex: index,
                columnId: "volume",
                newValue: dataItem.volume
            });
            }
        };
        MockServer.prototype.makeSomePriceChanges = function(changes) {
            for (var i = 0; i < 10; i++) {
            var index = Math.floor(this.allData.length * Math.random());
            var dataItem = this.allData[index];
            var move = Math.floor(30 * Math.random()) / 10 - 1;
            var newValue = dataItem.mid + move;
            dataItem.mid = newValue;
            this.setBidAndAsk(dataItem);
            changes.push({
                rowIndex: index,
                columnId: "mid",
                newValue: dataItem.mid
            });
            changes.push({
                rowIndex: index,
                columnId: "bid",
                newValue: dataItem.bid
            });
            changes.push({
                rowIndex: index,
                columnId: "ask",
                newValue: dataItem.ask
            });
            }
        };
        MockServer.prototype.init = function(allData) {
            this.allData = allData;
            var that = this;
            this.allData.forEach(function(dataItem) {
            dataItem.volume = Math.floor(Math.random() * 10000 + 100);
            dataItem.mid = Math.random() * 300 + 20;
            that.setBidAndAsk(dataItem);
            });
        };
        MockServer.prototype.setBidAndAsk = function(dataItem) {
            dataItem.bid = dataItem.mid * 0.98;
            dataItem.ask = dataItem.mid * 1.02;
        };
        MockServer.prototype.connect = function(listener) {
            var connectionId = this.nextConnectionId;
            this.nextConnectionId++;
            this.connections[connectionId] = {
            listener: listener,
            rowsInClient: {},
            firstRow: 0,
            lastRow: -1
            };
            this.sendEventAsync(connectionId, {
            eventType: "rowCountChanged",
            rowCount: this.allData.length
            });
            return connectionId;
        };
        MockServer.prototype.sendEventAsync = function(connectionId, event) {
            var listener = this.connections[connectionId].listener;
            setTimeout(function() {
            listener(event);
            }, 20);
        };
        MockServer.prototype.disconnect = function(connectionId) {
            delete this.connections[connectionId];
        };
        MockServer.prototype.setViewportRange = function(connectionId, firstRow, lastRow) {
            var connection = this.connections[connectionId];
            connection.firstRow = firstRow;
            connection.lastRow = lastRow;
            this.purgeFromClientRows(connection.rowsInClient, firstRow, lastRow);
            this.sendResultsToClient(connectionId, firstRow, lastRow);
        };
        MockServer.prototype.purgeFromClientRows = function(rowsInClient, firstRow, lastRow) {
            Object.keys(rowsInClient).forEach(function(rowIndexStr) {
            var rowIndex = parseInt(rowIndexStr);
            if (rowIndex < firstRow || rowIndex > lastRow) {
                delete rowsInClient[rowIndex];
            }
            });
        };
        MockServer.prototype.sendResultsToClient = function(connectionId, firstRow, lastRow) {
            if (firstRow < 0 || lastRow < firstRow) {
            console.warn("start or end is not valid");
            return;
            }
            var rowsInClient = this.connections[connectionId].rowsInClient;
            var rowDataMap = {};
            for (var i = firstRow; i <= lastRow; i++) {
            if (rowsInClient[i]) {
                continue;
            }
            rowDataMap[i] = this.allData[i];
            rowsInClient[i] = true;
            }
            this.sendEventAsync(connectionId, {
            eventType: "rowData",
            rowDataMap: rowDataMap
            });
        };
        MockServer.prototype.getRowCount = function() {
            return this.allData.length;
        };
        return new MockServer();
        }

</script>

<style>

.cell-number {
  text-align: right;
}

</style>
