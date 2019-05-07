/**
 * <p>Title: ResultTable.java</p>
 * <p>Description: ģ��ResultSet���࣬Ϊ��ҳ��ѯ����</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <P>All Rights Reserved.</p>
 * <p>Company: Advanced Digital Technology Co.Ltd.</p>
 * @author ������
 * @version 2.0
 */
package com.ripple.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * 2005-04-18:��ӷ�ҳ�������ã������
 * <p>��װ�˷�ҳ��ѯ�Ľ�����������ṩ����ResultSet�Ĳ����ӿ�</p>
 * @author
 * @version 1.1  2006/08/17 
 * @see DefaultSqlOperator
 */
public class ResultTable {

  private ArrayList resultArray = null;
  private ArrayList fldArray = null;
  private int nRow = 0;
  private int nCol = 0;
  private int nCurtRow = 0;
  private int nMaxRow = 0;

  private int iRecordCount = 0; // ��¼��
  private int iPageCount = 0; //��ҳ��

  private int iPageth = 1; //��ǰҳ��
  private int iPageSize = 20; //ÿҳ����¼��

  private HashMap map = new HashMap(); //����map

  private String actionUrl = null;

  private String style = "class='w01_11px_grey'";

  private String pagethParamName = "pageth";

  public ResultTable() {
  }

  /**
   * ������
   * @param iPageth int
   * @param iPageSize int
   * @param url String
   */
  public ResultTable(int iPageth, int iPageSize, String url) {
    this.setPageth(iPageth);
    this.setPageSize(iPageSize);
    this.setUrl(url);
  }

  /**
   * ������
   * @param iPageth int
   */
  public void setPageth(int iPageth) {
    this.iPageth = iPageth;
  }

  /**
   * ������
   * @param iPageSize int
   */
  public void setPageSize(int iPageSize) {
    this.iPageSize = iPageSize;
  }

  /**
   * ������
   * @param url String
   */
  public void setUrl(String url) {
    this.actionUrl = url;
  }

  /**
   * ������
   * @return int
   */
  public int getPageth() {
    return this.iPageth;
  }

  /**
   * ������
   * @return int
   */
  public int getPageSize() {
    return this.iPageSize;
  }

  /**
   * ������
   * @return String
   */
  public String getUrl() {
    return this.actionUrl;
  }

  /**
   * ������
   * ��������
   * @param name String
   * @param value String
   */
  public void putParameter(String name, String value) {
    map.put(name, value);
  }

  /**
   *
   * @return int
   * ������
   */
  public int getNextPage() {
    return this.iPageth + 1;
  }

  /**
   *
   * @return int
   * ������
   */
  public int getPreviousPage() {
    return this.iPageth - 1;
  }

  /**
   *
   * @return int
   * ������
   */
  public int getFirstPage() {
    return 1;
  }

  /**
   *
   * @return int
   * ������
   */
  public int getLastPage() {
    return this.iPageCount;
  }

  public void setPagethParamName(String pagethParamName) {
    this.pagethParamName = pagethParamName;
  }

  public String getPagethParamName() {
    return this.pagethParamName;
  }

  /**
   * ��ǰҳ�ļ�¼��
   * @return int
   */
  public int size() {
    return this.nRow;
  }

  /**
   *
   *
   <script>
    function turnPage(forward) {
        if(forward == "1"){
                form1.Pageth.value="<%=iPageth+1%>";
                form1.submit();
        }
        if(forward == "0"){
                form1.Pageth.value="<%=iPageth-1%>";
                form1.submit();
        }
   }

    function toPage(forward) {
                form1.Pageth.value= forward;
                form1.submit();

   }

   </script>

   */
  /**
   *
   * @return String
   * ������
   */
  public String getTurnPageScript() {
    StringBuffer link = new StringBuffer();

    if (iPageCount > 1) {

      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>ҳ");

      if (iPageth > 1) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:toPage('1')\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:turnPage('0')\"><font color='blue'>��ҳ</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:turnPage('1')\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"javascript:toPage('" +
                    iPageCount + "')\"><font color='blue'>βҳ</font></a>");
      }
    }
    else {

      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>1/1</font>ҳ");

    }

    return link.toString();
  }

  /**
   *
   * @return String
   * ������
   */
  public String getTurnPageString() {
    StringBuffer link = new StringBuffer();
    String condStr = "";

    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>ҳ");

      if (iPageth > 1) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getPreviousPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a class='w01_11px_grey'  href=\"" + this.actionUrl +
                    "?Pageth=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>βҳ</font></a>");
      }
    }
    else {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>1/1</font>ҳ");

    }

    return link.toString();

  }

  /**��ҳ��*/
  public String getTurnPager() {

    StringBuffer link = new StringBuffer();
    String condStr = "";
    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>ҳ");

      if (iPageth > 1) {

        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getPreviousPage() +
                    condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>βҳ</font></a>");
      }
    }
    else {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>1/1</font>ҳ");

    }

    return link.toString();

  }

  public String getTurnPagerWithInput() {

    StringBuffer link = new StringBuffer();
    String condStr = "";
    if (map != null && map.size() > 0) {
      Iterator it = map.keySet().iterator();
      while (it.hasNext()) {
        String key = (String) it.next();
        String temp = (String) map.get(key);
        temp = (temp == null) ? "" : temp.trim();
        condStr += "&" + key + "=" + temp;
      }
    }

    if (iPageCount > 1) {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>" +
                  this.getPageth() + "/" + this.getPageNum() + "</font>ҳ");

      if (iPageth > 1) {

        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getFirstPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getPreviousPage() +
                    condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
      }

      if (iPageth < iPageCount) {
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getNextPage() + condStr +
                    "\"><font color='blue'>��ҳ</font></a>");
        link.append("&nbsp;&nbsp;");
        link.append("<a " + style + "  href=\"" + this.actionUrl +
                    "?" + pagethParamName + "=" + this.getLastPage() + condStr +
                    "\"><font color='blue'>βҳ</font></a>");
      }
    }
    else {
      link.append("��<font color='red'>" + this.getCount() +
                  "</font>����¼,��<font color='red'>1/1</font>ҳ");

    }

    link.append("&nbsp;&nbsp;");
    link.append("<input type=\"text\"   name='"+this.pagethParamName+"' ><input type='button' value='GO' name='go'>");
    link.append("&nbsp;&nbsp;");

    return link.toString();
  }

  /**
   * create result table
   * @param nCol
   */
  public ResultTable(int nCol) {

    this.nCol = nCol;
    resultArray = new ArrayList();
    fldArray = new ArrayList(nCol);
    for (int i = 0; i < nCol; i++) {
      fldArray.add(i, null);
    }
    nCurtRow = -1;
    nMaxRow = 0;

  }

  /**
   *  ����Ҫȡ�����ʱ�����Ƚ��α�λ�õ������������
   */
  public void first() {
    nCurtRow = -1;
  }

  /**
   *  get Cursor Position
   * @return ���ص�ǰ�α��λ��
   */
  public int getPosition() {
    return nCurtRow;
  }

  /**
   *  insert new row to Result table.
   */
  public void insertRow() {
    nCurtRow = nMaxRow;
    ArrayList rowArray = new ArrayList(nCol);
    for (int i = 0; i < nCol; i++) {
      rowArray.add(i, null);
    }
    resultArray.add(nCurtRow, rowArray);
    nMaxRow++;
  }

  /**
   * �жϵ�ǰ�α��λ���Ƿ�������
   * @return true ���ǣ�false ��
   */
  public boolean isEof() {
    if (nCurtRow < nMaxRow) {
      return true;
    }
    return false;
  }

  /**
   * �α�λ������һλ
   * @return �Ƿ�ɹ�
   */
  public boolean next() {
    if (nCurtRow < (nMaxRow - 1) && nMaxRow != 0) {
      nCurtRow++;
      return true;
    }

    return false;
  }

  /**
   * �α�λ��ǰ��һλ
   * @return �Ƿ�ɹ�
   */
  public boolean before() {
    if (nCurtRow >= 0) {
      nCurtRow--;
      return true;
    }
    return false;
  }

  /**
   * ��λ�α�ľ���λ��
   * @param λ��ƫ����
   * @return �Ƿ�ɹ�
   */
  public boolean absolute(int offset) {
    if (offset >= 0 && offset < nMaxRow) {
      nCurtRow = offset;
      return true;
    }

    return false;
  }

  /**
   * get result string by position
   * @param colnum
   * @return
   */
  public String getString(int colnum) {
    String str = null;
    if (colnum >= 0 && colnum < nCol) {
      if (nCurtRow == -1) {
        nCurtRow = 0;
      }
      ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
      str = (String) rowArray.get(colnum);
    }
    return str;
  }

  /**
   * get result string by field name
   * @param fldName
   * @return
   */
  public String getString(String fldName) {
    String str = null;
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    for (int i = 0; i < this.nCol; i++) {
      if (fldName.compareToIgnoreCase( (String) fldArray.get(i)) == 0) {
        ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
        str = (String) rowArray.get(i);
        break;
      }
    }

    return str;
  }

  /**
   * set field name
   * @param nCol
   * @param fldName
   * @return
   */
  public boolean setField(int nCol, String fldName) {
    if (nCol >= 0 && nCol < this.nCol) {
      fldArray.set(nCol, fldName);
      return true;
    }

    return false;
  }

  /**
   * set result value by position
   * @param iCol
   * @param val
   * @return
   */
  public boolean setValue(int nCol, String val) {
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    if (nCol >= 0 && nCol < this.nCol) {
      ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
      rowArray.set(nCol, val);
      return true;
    }
    return false;
  }

  /**
   * set result value by field name
   * @param fldName
   * @param val
   * @return
   */
  public boolean setValue(String fldName, String val) {
    if (nCurtRow == -1) {
      nCurtRow = 0;
    }
    for (int i = 0; i < nCol; i++) {
      // ��ѯƥ����ֶ�λ��
      if (fldName.compareToIgnoreCase( (String) fldArray.get(i)) == 0) {
        ArrayList rowArray = (ArrayList) resultArray.get(nCurtRow);
        if (i >= resultArray.size()) {

        }
        rowArray.set(i, val);
        return true;
      }
    }

    return false;
  }

  /**
   * ���õ�ǰ������ļ�¼��
   * @param nCount
   */
  public void setCount(int nCount) {
    this.iRecordCount = nCount;
  }

  /**
   * ��õ�ǰ������ļ�¼��
   * @return
   */
  public int getCount() {
    return this.iRecordCount;
  }

  /**
   * ���õ�ǰ��ҳ����Ŀ
   * @param nPageNum
   */
  public void setPageNum(int nPageNum) {
    this.iPageCount = nPageNum;
  }

  /**
   * ��õ�ǰ��ҳ����Ŀ
   * @return
   */
  public int getPageNum() {
    return this.iPageCount;
  }

  /**
   * ���õ�ǰҳ�ļ�¼������Fancy Added 2004��03��13
   * @param row ��ǰ��¼��
   */
  public void setRow(int row) {
    this.nRow = row;
  }

  /**
   * ���ص�ǰҳ�ļ�¼������Fancy Added 2004��03��13
   * @return ��ǰ��¼��
   */
  public int getRow() {
    return this.nRow;
  }
  
  public int getColCount() {
	  return this.nCol;
  }

}
