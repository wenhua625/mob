/* 
 * Ĭ��ת��ʵ�ֺ����������Ҫ�������ܣ���������չ
 * ������
 *      tableID : HTML��Table����id����ֵ
 * ��ϸ�÷��μ����� TableToExcel ������  
 */
function saveAsExcel(tableID){
 var tb = new TableToExcel(tableID);
  tb.setFontStyle("Courier New");
  tb.setFontSize(10);
  tb.setTableBorder(2);
  tb.setColumnWidth(7);
  tb.isLineWrap(true);
  tb.getExcelFile();
}

/*
 *  ���ܣ�HTML��Table����ת��ΪExcelͨ�ö���.
 *  ���ߣ�Jeva
 *  ʱ�䣺2006-08-09
 *  ������tableID  HTML��Table�����ID����ֵ
 *  ˵����
 *       ����Ӧ���ӵ�HTML��Table������Զ�ת�����ܹ��Զ�����������չ��Ϣ
 *       �ϲ�Excel�еĵ�Ԫ�񣬿ͻ�����Ҫ��װ��Excel
 *       ��ϸ�����ԡ���������˵���μ���Excel��Microsoft Excel Visual Basic�ο�
 *  ʾ����
 *       var tb = new TableToExcel('demoTable');
 *    tb.setFontStyle("Courier New");
 *    tb.setFontSize(10);  //�Ƽ�ȡֵ10
 *    tb.setFontColor(6);  //һ�������������
 *    tb.setBackGround(4);  //һ�������������
 *    tb.setTableBorder(2);  //�Ƽ�ȡֵ2
 *    tb.setColumnWidth(10);  //�Ƽ�ȡֵ10
 *    tb.isLineWrap(false);
 *    tb.isAutoFit(true);
 *    
 *    tb.getExcelFile();
 *   ��������˵�Ԫ������Ӧ�������õ�Ԫ������Ч
 *  �汾��1.0
 */
function TableToExcel(tableID) {
    this.tableBorder = -1; //�߿����ͣ�-1û�б߿� ��ȡ1/2/3/4
    this.backGround = 0; //������ɫ����ɫ   ��ȡ��ɫ���е���ɫ��� 1/2/3/4....
    this.fontColor = 1;  //������ɫ����ɫ
    this.fontSize = 10;  //�����С
    this.fontStyle = "����"; //��������
    this.rowHeight = -1; //�и�
    this.columnWidth = -1; //�п�
    this.lineWrap = true; //�Ƿ��Զ�����
    this.textAlign = -4108; //���ݶ��뷽ʽ   Ĭ��Ϊ����
    this.autoFit = false;  //�Ƿ�����Ӧ���
    this.tableID = tableID; 
}

TableToExcel.prototype.setTableBorder = function (excelBorder) {
    this.tableBorder = excelBorder ;
};

TableToExcel.prototype.setBackGround = function (excelColor) {
    this.backGround = excelColor;
};

TableToExcel.prototype.setFontColor = function (excelColor) {
    this.fontColor = excelColor;
};

TableToExcel.prototype.setFontSize = function (excelFontSize) {
    this.fontSize = excelFontSize;
};

TableToExcel.prototype.setFontStyle = function (excelFont) {
    this.fontStyle = excelFont;
};

TableToExcel.prototype.setRowHeight = function (excelRowHeight) {
    this.rowHeight = excelRowHeight;
};

TableToExcel.prototype.setColumnWidth = function (excelColumnWidth) {
    this.columnWidth = excelColumnWidth;
};

TableToExcel.prototype.isLineWrap = function (lineWrap) {
    if (lineWrap == false || lineWrap == true) {
        this.lineWrap = lineWrap;
    }
};

TableToExcel.prototype.setTextAlign = function (textAlign) {
    this.textAlign = textAlign;
};

TableToExcel.prototype.isAutoFit = function(autoFit){
 if(autoFit == true || autoFit == false)
  this.autoFit = autoFit ;
}

//�ļ�ת��������
TableToExcel.prototype.getExcelFile = function () {
    var jXls, myWorkbook, myWorksheet, myHTMLTableCell, myExcelCell, myExcelCell2;
    var myCellColSpan, myCellRowSpan;

    try {
        jXls = new ActiveXObject('Excel.Application');
    }
    catch (e) {
        alert("�޷�����Excel!\n\n" + e.message + 
           "\n\n�����ȷ�����ĵ������Ѿ���װ��Excel��"+
           "��ô�����IE�İ�ȫ����\n\n���������\n\n"+
           "���� �� Internetѡ�� �� ��ȫ �� �Զ��弶�� �� ActiveX�ؼ��Ͳ�� \n\n" +
       "�� ���� : ��û�б��Ϊ��ȫ��ActiveX�ؼ����г�ʼ���ͽű�����");
        return false;
    }

    jXls.Visible = true;
    myWorkbook = jXls.Workbooks.Add();
    jXls.DisplayAlerts = false;
    myWorkbook.Worksheets(3).Delete();
    myWorkbook.Worksheets(2).Delete();
    jXls.DisplayAlerts = true;
    myWorksheet = myWorkbook.ActiveSheet;
    
    var  readRow = 0,  readCol = 0;
    var totalRow = 0, totalCol = 0;
    var   tabNum = 0;
    
//�����иߡ��п�
    if(this.columnWidth != -1)
     myWorksheet.Columns.ColumnWidth = this.columnWidth;
    else
     myWorksheet.Columns.ColumnWidth = 7;
    if(this.rowHeight != -1)
     myWorksheet.Rows.RowHeight = this.rowHeight ;

//������Ҫת����Table���󣬻�ȡ��Ӧ�С�����
    var obj = document.all.tags("table");
    for (x = 0; x < obj.length; x++) {
        if (obj[x].id == this.tableID) {
            tabNum = x;
            totalRow = obj[x].rows.length;
            for (i = 0; i < obj[x].rows[0].cells.length; i++) {
                myHTMLTableCell = obj[x].rows(0).cells(i);
                myCellColSpan = myHTMLTableCell.colSpan;
                totalCol = totalCol + myCellColSpan;
            }
        }
    }

//��ʼ����ģ����
    var excelTable = new Array();
    for (i = 0; i <= totalRow; i++) {
        excelTable[i] = new Array();
        for (t = 0; t <= totalCol; t++) {
            excelTable[i][t] = false;
        }
    }
    
//��ʼת�����    
    for (z = 0; z < obj[tabNum].rows.length; z++) {
        readRow = z + 1;
        readCol = 0;
        for (c = 0; c < obj[tabNum].rows(z).cells.length; c++) {
            myHTMLTableCell = obj[tabNum].rows(z).cells(c);
            myCellColSpan = myHTMLTableCell.colSpan;
            myCellRowSpan = myHTMLTableCell.rowSpan;
            for (y = 1; y <= totalCol; y++) {
                if (excelTable[readRow][y] == false) {
                    readCol = y;
                    break;
                }
            }
            if (myCellColSpan * myCellRowSpan > 1) {
                myExcelCell = myWorksheet.Cells(readRow, readCol);
                myExcelCell2 = myWorksheet.Cells(readRow + myCellRowSpan - 1, readCol + myCellColSpan - 1);
                myWorksheet.Range(myExcelCell, myExcelCell2).Merge();
                myExcelCell.HorizontalAlignment = this.textAlign;
                myExcelCell.Font.Size = this.fontSize;
                myExcelCell.Font.Name = this.fontStyle;
                myExcelCell.wrapText = this.lineWrap;
                myExcelCell.Interior.ColorIndex = this.backGround;
                myExcelCell.Font.ColorIndex = this.fontColor;
                if(this.tableBorder != -1){
                 myWorksheet.Range(myExcelCell, myExcelCell2).Borders(1).Weight = this.tableBorder ;
                 myWorksheet.Range(myExcelCell, myExcelCell2).Borders(2).Weight = this.tableBorder ;
                 myWorksheet.Range(myExcelCell, myExcelCell2).Borders(3).Weight = this.tableBorder ;
                 myWorksheet.Range(myExcelCell, myExcelCell2).Borders(4).Weight = this.tableBorder ;
                }

                myExcelCell.Value = myHTMLTableCell.innerText;
                for (row = readRow; row <= myCellRowSpan + readRow - 1; row++) {
                    for (col = readCol; col <= myCellColSpan + readCol - 1; col++) {
                        excelTable[row][col] = true;
                    }
                }
                
                readCol = readCol + myCellColSpan;
            } else {
                myExcelCell = myWorksheet.Cells(readRow, readCol);
                myExcelCell.Value = myHTMLTableCell.innerText;
                myExcelCell.HorizontalAlignment = this.textAlign;
                myExcelCell.Font.Size = this.fontSize;
                myExcelCell.Font.Name = this.fontStyle;
                myExcelCell.wrapText = this.lineWrap;
                myExcelCell.Interior.ColorIndex = this.backGround;
                myExcelCell.Font.ColorIndex = this.fontColor;
                if(this.tableBorder != -1){
                 myExcelCell.Borders(1).Weight = this.tableBorder ;
                 myExcelCell.Borders(2).Weight = this.tableBorder ;
                 myExcelCell.Borders(3).Weight = this.tableBorder ;
                 myExcelCell.Borders(4).Weight = this.tableBorder ;
                }
                excelTable[readRow][readCol] = true;
                readCol = readCol + 1;
            }
        }
    }
    if(this.autoFit == true)
     myWorksheet.Columns.AutoFit;
     
    jXls.UserControl = true;
    jXls = null;
    myWorkbook = null;
    myWorksheet = null;
};

