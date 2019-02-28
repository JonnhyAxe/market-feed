<template>
    <div style="width: 100%;">
        <div style="padding: 4px;">
            <div style="float: left;">
                <input @keyup="onQuickFilterChanged" type="text" id="quickFilterInput"
                       placeholder="Type text to filter..."/>
            </div>
        </div>
        <div style="clear: both;"></div>
        <div v-if="showGrid">
            <div style="clear: both;"></div>
            <ag-grid-vue style="width: 100%; height: 400px;" class="ag-theme-balham-dark"
                         :gridOptions="gridOptions"
                         :columnDefs="columnDefs"
                         :rowData="rowData"
                         :sideBar="sideBar"

                         :enableColResize="true"
                         :enableSorting="true"
                         :enableFilter="true"
                         :groupHeaders="true"
                         :suppressRowClickSelection="true"
                         :rowSelection="multiple"
                         :getRowNodeId="getRowNodeId"

                         :modelUpdated="onModelUpdated"
                         :rowDataChanged="onRowDataChanged"
                         :cellClicked="onCellClicked"
                         :cellDoubleClicked="onCellDoubleClicked"
                         :cellContextMenu="onCellContextMenu"
                         :cellValueChanged="onCellValueChanged"
                         :cellFocused="onCellFocused"
                         :rowSelected="onRowSelected"
                         :selectionChanged="onSelectionChanged"
                         :beforeFilterChanged="onBeforeFilterChanged"
                         :afterFilterChanged="onAfterFilterChanged"
                         :filterModified="onFilterModified"
                         :beforeSortChanged="onBeforeSortChanged"
                         :afterSortChanged="onAfterSortChanged"
                         :virtualRowRemoved="onVirtualRowRemoved"
                         :rowClicked="onRowClicked"
                         :gridReady="onReady"

                         :columnEverythingChanged="onColumnEvent"
                         :columnRowGroupChanged="onColumnEvent"
                         :columnValueChanged="onColumnEvent"
                         :columnMoved="onColumnEvent"
                         :columnVisible="onColumnEvent"
                         :columnGroupOpened="onColumnEvent"
                         :columnResized="onColumnEvent"
                         :columnPinnedCountChanged="onColumnEvent">
            </ag-grid-vue>
        </div>
    </div>
</template>

<script>
    import Vue from "vue";
    import {AgGridVue} from "ag-grid-vue";
    import axios from 'axios'
    import { Observable } from "rxjs"

    export default {
        data() {
            return {
                gridApi: null,
                gridOptions: null,
                columnDefs: null,
                rowData: [],
                showGrid: false,
                sideBar: true,
                rowCount: null,
                observable: null,
                subscription: null,
                actual_msg: '',
                total_items: -1,
                items: [],
                loading: false,
                evtSource: null,
                getRowNodeId: null
            }
        },
        beforeCreate() {
            console.log('Nothing gets called before me!')
        },
        created(){ 
            console.log('created ' + this.rowData);
            this.setupStream();
        },
        mounted(){
            console.log('mounted ' + this.rowData);
  
            this.gridApi = this.gridOptions.api;
            this.gridColumnApi = this.gridOptions.columnApi;

        },
        search(item, code) {
            return item.code === code;
        },
        components: {
            AgGridVue
        },
        methods: {
            mountedMethod(){
             
            },
            createRowData() {
                const httpRequest = new XMLHttpRequest();

                httpRequest.open("GET", "http://localhost:8080/markets");
                httpRequest.send();
                httpRequest.onreadystatechange = () => {
                    if (httpRequest.readyState === 4 && httpRequest.status === 200) {
                        const rowDataTmp = [];
                        JSON.parse(httpRequest.responseText)
                            .forEach(function(item) {
                                rowDataTmp.push({
                                    code: item.code,
                                    name: item.name,
                                    bid:  item.bid != undefined ? item.bid : '1',
                                    mid: item.mid != undefined ? item.mid : '2',
                                    ask: item.ask != undefined ? item.ask : '3',
                                    volume: item.volume != undefined ? item.volume : '12'                           
                            }); 
                        });
                        this.rowData = rowDataTmp;

                    }   
                }
            },
            createColumnDefs() {
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
            },
            pad(num, totalStringSize) {
                let asString = num + "";
                while (asString.length < totalStringSize) asString = "0" + asString;
                return asString;
            },

            calculateRowCount() {
                if (this.gridOptions.api && this.rowData) {
                    let model = this.gridOptions.api.getModel();
                    let totalRows = this.rowData.length;
                    let processedRows = model.getRowCount();
                    this.rowCount = processedRows.toLocaleString() + ' / ' + totalRows.toLocaleString();
                }
            },
            onRowDataChanged() {
                Vue.nextTick(() => {
                        console.log('onRowDataChanged')
                        this.gridOptions.api.sizeColumnsToFit();
                    }
                );
            },
            onModelUpdated() {
                console.log('onModelUpdated');
                this.calculateRowCount();
            },
            onReady() {
                this.calculateRowCount();
            },
            setupStream() {

                console.log('onReady' + this.rowData);
                // Not a real URL, just using for demo purposes
                let es = new EventSource('http://localhost:8080/topic/price-updates');

                es.addEventListener('message', event => {
                    if(event.data !== 'heartbeat...') {
                            //https://www.ag-grid.com/javascript-grid-refresh/

                            let item = JSON.parse(event.data);
                            var rowNode = this.gridApi.getRowNode(item.code);
                            rowNode.setDataValue("bid", item.bid);
                            rowNode.setDataValue("mid", item.mid);
                            rowNode.setDataValue("ask", item.ask);
                            rowNode.setDataValue("volume", item.volume);
                            return; 
                        }
                }, false);

                es.addEventListener('error', event => {
                    if (event.readyState == EventSource.CLOSED) {
                        console.log('Event was closed');
                        console.log(EventSource);
                    }
                    }, false);
            },
            onCellClicked(event) {
                console.log('onCellClicked: ' + event.rowIndex + ' ' + event.colDef.field);
            },

            onCellValueChanged(event) {
                console.log('onCellValueChanged: ' + event.oldValue + ' to ' + event.newValue);
            },

            onCellDoubleClicked(event) {
                console.log('onCellDoubleClicked: ' + event.rowIndex + ' ' + event.colDef.field);
            },

            onCellContextMenu(event) {
                console.log('onCellContextMenu: ' + event.rowIndex + ' ' + event.colDef.field);
            },

            onCellFocused(event) {
                console.log('onCellFocused: (' + event.rowIndex + ',' + event.colIndex + ')');
            },

            // taking out, as when we 'select all', it prints to much to the console!!
            // eslint-disable-next-line
            onRowSelected(event) {
                console.log('onRowSelected: ' + event.node.data.name);
            },

            onSelectionChanged() {
                console.log('selectionChanged');
            },

            onBeforeFilterChanged() {
                console.log('beforeFilterChanged');
            },

            onAfterFilterChanged() {
                console.log('afterFilterChanged');
            },

            onFilterModified() {
                console.log('onFilterModified');
            },

            onBeforeSortChanged() {
                console.log('onBeforeSortChanged');
            },

            onAfterSortChanged() {
                console.log('onAfterSortChanged');
            },

            // eslint-disable-next-line
            onVirtualRowRemoved(event) {
                // because this event gets fired LOTS of times, we don't print it to the
                // console. if you want to see it, just uncomment out this line
                // console.log('onVirtualRowRemoved: ' + event.rowIndex);
            },

            onRowClicked(event) {
                console.log('onRowClicked: ' + event.node.data.name);
            },

            onQuickFilterChanged(event) {
                this.gridOptions.api.setQuickFilter(event.target.value);
            },

            // here we use one generic event to handle all the column type events.
            // the method just prints the event name
            onColumnEvent(event) {
                console.log('onColumnEvent: ' + event);
            }
        },
        beforeMount() {
            console.log(`this.$el doesn't exist yet, but it will soon!`)
            this.gridOptions = {};
            this.createRowData();
            this.createColumnDefs();
  
            this.showGrid = true;
            this.getRowNodeId = data => {
                return data.code;
            };
            this.components = {
                rowIdRenderer: params => {
                    return "" + params.rowIndex;
                }
            }
        }
    }



    function numberFormatter(params) {
        if (typeof params.value === "number") {
            return params.value.toFixed(2);
        } else {
            return params.value;
        }
    }

</script>

<style>
    .ag-cell {
        padding-top: 2px !important;
        padding-bottom: 2px !important;
    }

    label {
        font-weight: normal !important;
    }

    .div-percent-bar {
        display: inline-block;
        height: 100%;
        position: relative;
        z-index: 0;
    }

    .div-percent-value {
        position: absolute;
        padding-left: 4px;
        font-weight: bold;
        font-size: 13px;
        z-index: 100;
    }

    .div-outer-div {
        display: inline-block;
        height: 100%;
        width: 100%;
    }

    .ag-menu {
        z-index: 200;
    }

    .toolbar button {
        margin-left: 5px;
        margin-right: 5px;
        padding: 2px;
    }
</style>
