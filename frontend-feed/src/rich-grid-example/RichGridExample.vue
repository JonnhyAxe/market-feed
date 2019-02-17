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
                         rowSelection="multiple"

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
    import {ProficiencyFilter} from './proficiencyFilter';
    import HeaderGroupComponent from './HeaderGroupComponent.vue';
    import RefData from './refData'
    import axios from 'axios'
    
    export default {
        data() {
            return {
                gridOptions: null,
                columnDefs: null,
                rowData: null,
                showGrid: false,
                sideBar: true,
                rowCount: null
            }
        },
        components: {
            AgGridVue
        },
        methods: {
            createRowData() {
                const rowDataTmp = [];

                for (let i = 0; i < 200; i++) {
                    const websiteData = RefData.WEBSITES[i % RefData.WEBSITES.length];
                    rowDataTmp.push({
                        name: RefData.FIRST_NAMES[i % RefData.FIRST_NAMES.length] + ' ' + RefData.LAST_NAMES[i % RefData.LAST_NAMES.length],
                        id: RefData.IDS[i % RefData.IDS.length],
                        energy: Math.round(Math.random() * 10),
                        websites: websiteData.uri,
                        continent: websiteData.continent,
                        language: websiteData.language
                    });
                }
                this.rowData = rowDataTmp;
            },
            getMusics () {
                const rowDataTmp = [];
                let path = window.location.protocol + '//' + window.location.hostname + ':' + window.location.port;
                axios
                .get(path + '/music')
                .then(response => {
                    // JSON responses are automatically parsed.
                    if(response.data !== undefined) {
                        response.data.forEach(function(item) {
                            rowDataTmp.push({
                                name: item.name,
                                id: item.id,
                                artits:  item.artists.toString(),
                                style: item.style,
                                remixers: item.remixers != undefined ? item.remixers.toString() : '',
                                energy: item.energy != undefined ? item.energy: '',
                                websites:  item.websites != undefined ? item.websites.toString() : '',
                                remixers:  item.remixers != undefined ? item.remixers.toString() : '',
                                recordLabel: item.recordLabel != undefined ? item.recordLabel : '',
                                key: item.key != undefined ? item.key : '',
                                tempo: item.tempo != undefined ? item.tempo : ''                             
                                
                            });  
                        });
                    }
                    console.log(response.status)
                    console.log(response.data)
                    this.rowData = rowDataTmp;
                }).catch(e => {
                    //this.errors.push(e)
                     console.log('Error reading data ' + e)
                    this.rowData = [];
                })
            },
            createColumnDefs() {
                this.columnDefs = [
                    {
                        headerName: '#', width: 30, checkboxSelection: true, suppressSorting: true,
                        suppressMenu: true, pinned: true
                    },
                    {
                        headerName: 'Music',
                        children: [
                            {
                                headerName: "ID",
                                field: "id",
                                width: 160,
                                suppressSorting: true                            
                            },
                            {
                                headerName: "Artits",
                                field: "artits",
                                width: 100,
                                suppressSorting: true
                            },
                            {
                                headerName: "Name",
                                field: "name",
                                width: 200,
                                suppressSorting: true
                            }, 
                            {
                                headerName: "Style",
                                field: "style",
                                width: 100,
                                suppressSorting: true
                            },
                            {
                                headerName: "Remixers",
                                field: "remixers",
                                width: 100,
                                suppressSorting: true
                            },
                            {
                                headerName: "Record Label",
                                field: "recordLabel",
                                width: 100,
                                suppressSorting: true
                            },                                                                                   
                            {
                                headerName: "Energy",
                                field: "energy",
                                width: 120,
                                cellRenderer: percentCellRenderer,
                                filter: ProficiencyFilter
                            },
                            {
                                headerName: "Key",
                                field: "key",
                                width: 60,
                                suppressSorting: true
                            },
                            {
                                headerName: "BPM",
                                field: "tempo",
                                width: 80,
                                suppressSorting: true
                            },
                            {
                                headerName: "Location",
                                field: "locaion",
                                width: 150,
                                suppressSorting: true
                            }                             
                        ]
                    }                   
                ];
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
                        this.gridOptions.api.sizeColumnsToFit();
                    }
                );
            },
            onModelUpdated() {
                console.log('onModelUpdated');
                this.calculateRowCount();
            },

            onReady() {
                console.log('onReady');
                this.calculateRowCount();
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
                // console.log('onRowSelected: ' + event.node.data.name);
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
            this.gridOptions = {};
            this.getMusics();
            this.createColumnDefs();
            this.showGrid = true;
        }
    }

    function countryCellRenderer(params) {
        let flag = "<img border='0' width='15' height='10' style='margin-bottom: 2px' src='https://raw.githubusercontent.com/ag-grid/ag-grid-docs/master/src/images/flags/" + RefData.COUNTRY_CODES[params.value] + ".png'>";
        return flag + " " + params.value;
    }

    function createRandomPhoneNumber() {
        let result = '+';
        for (let i = 0; i < 12; i++) {
            result += Math.round(Math.random() * 10);
            if (i === 2 || i === 5 || i === 8) {
                result += ' ';
            }
        }
        return result;
    }

    function percentCellRenderer(params) {
        let value = params.value;

        let eDivPercentBar = document.createElement('div');
        eDivPercentBar.className = 'div-percent-bar';
        eDivPercentBar.style.width = value * 10 + '%';
        if (value < 2) {
            eDivPercentBar.style.backgroundColor = 'red';
        } else if (value < 6) {
            eDivPercentBar.style.backgroundColor = '#ff9900';
        } else {
            eDivPercentBar.style.backgroundColor = '#00A000';
        }

        let eValue = document.createElement('div');
        eValue.className = 'div-percent-value';
        eValue.innerHTML = value ;

        let eOuterDiv = document.createElement('div');
        eOuterDiv.className = 'div-outer-div';
        eOuterDiv.appendChild(eValue);
        eOuterDiv.appendChild(eDivPercentBar);

        return eOuterDiv;
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
