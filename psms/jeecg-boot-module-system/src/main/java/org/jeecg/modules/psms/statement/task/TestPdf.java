package org.jeecg.modules.psms.statement.task;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import org.jeecg.modules.demo.statementdata.entity.BaseStatementData;
import org.jeecg.modules.utils.ToolsUtils;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TestPdf {
    public String createPDF(String filename,List<BaseStatementData>listData,int type,int days) throws IOException {
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.addTitle("example of PDF");
            document.open();

            PdfPTable tables = createTableTitle(type,days);
            document.add(tables);

            PdfPTable table = createTable(listData);
            document.add(table);
        } catch (Exception e) {
            return "";
        } finally {
            document.close();
        }
        return filename;
    }
    private PdfPTable createTableTitle(int type,int days) throws DocumentException, IOException{
        //String p = getContent("static/tts/simhei.ttf");
        BaseFont baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);//系统字体
        Font font = new Font(baseFont, 9, Font.BOLD, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(1);
        int totalWidth = 560;
        table.setTotalWidth(totalWidth);
        table.setLockedWidth(true);
        String title = "餐饮油烟排放浓度在线监测分析报告";
        switch (type){
            case 1:{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DATE,-1);
                Date date =  c.getTime();
                String format = sdf.format(date);

                Calendar c2 = Calendar.getInstance();
                c2.add(Calendar.DATE,-days);
                Date date2 = c2.getTime();
                String format2 = sdf.format(date2);

                title = title+"("+format2+"-"+format+")";
            }break;
            case 2:{
                String format = ToolsUtils.getLastMonth(1);
                title = title+"("+format+")";
            }break;
            case 3:{
                String format1 = ToolsUtils.getLastMonth(1);
                String format2 = ToolsUtils.getLastMonth(2);
                String format3 = ToolsUtils.getLastMonth(3);
                title = title+"("+format3+"~"+format2+"~"+format1+")";
            }break;
        }

        PdfPCell cell = new PdfPCell(new Phrase(title,font));
        cell.setFixedHeight(20);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(0);
        cell.setBottom(5);
        table.addCell(cell);
        return table;
    }
    private PdfPTable createTable(List<BaseStatementData>listData) throws DocumentException, IOException{

        String[] title ={"编码","企业名称", "测点MAC","监测天数","数据采集量","数据完整率",
                "风机开启时长","净化器联动时长","净化器联动率","油烟浓度0-1时长",
                "油烟浓度1.01-2时长","油烟浓度2.01-4时长","油烟浓度4.01+时长","超标总时长"};
        int[] widths = {5,25,15,7,14,14,10,12,9,11,12,12,12,8};
        PdfPTable table = new PdfPTable(title.length);
        int totalWidth = 560;
        table.setTotalWidth(totalWidth);
        table.setLockedWidth(true);
        table.setWidths(widths);
        PdfPCell cell = null;
        int size = 20;

        //simhei.ttf
        //String p = getContent("static/tts/simhei.ttf");
        BaseFont baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\simhei.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);//系统字体
        Font font = new Font(baseFont, 7, Font.BOLD, BaseColor.BLACK);
        for (String s : title) {
            cell = new PdfPCell(new Phrase(s,font));
            cell.setFixedHeight(size);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            table.addCell(cell);
        }

        font = new Font(baseFont, 7, Font.NORMAL, BaseColor.BLACK);
        for(int i = 0;i<listData.size();i++) {
            for(int j = 0;j<title.length;j++){
                switch (j){
                    case 0://编码
                        cell = new PdfPCell(new Phrase(String.valueOf(i+1),font));
                        break;
                    case 1://企业名称
                        cell = new PdfPCell(new Phrase(listData.get(i).getName(),font));
                        break;
                    case 2://测点MAC
                        cell = new PdfPCell(new Phrase(listData.get(i).getPointMac(),font));
                        break;
                    case 3://监测天数
                        cell = new PdfPCell(new Phrase(listData.get(i).getDays().toString()+"天",font));
                        break;
                    case 4://数据采集量
                        cell = new PdfPCell(new Phrase(listData.get(i).getDataAmount().toString(),font));
                        break;
                    case 5://数据完整率
                        cell = new PdfPCell(new Phrase(listData.get(i).getDataRate().toString()+"%",font));
                        break;
                    case 6://风机开启时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getDraughtTime().toString()+"h",font));
                        break;
                    case 7://净化器联动时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getLinkageTime().toString()+"h",font));
                        break;
                    case 8://净化器联动率
                        cell = new PdfPCell(new Phrase(listData.get(i).getPurifierLinkageRate().toString()+"%",font));
                        break;
                    case 9://油烟浓度0-1时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getLampblackHour1().toString()+"h",font));
                        break;
                    case 10://油烟浓度1.01-2时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getLampblackHour2().toString()+"h",font));
                        break;
                    case 11://油烟浓度2.01-4时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getLampblackHour3().toString()+"h",font));
                        break;
                    case 12://油烟浓度4.01+时长
                        cell = new PdfPCell(new Phrase(listData.get(i).getLampblackHour4().toString()+"h",font));
                        break;
                    case 13://超标总时长
                        int a = listData.get(i).getLampblackHour2()+listData.get(i).getLampblackHour3()+listData.get(i).getLampblackHour4();
                        cell = new PdfPCell(new Phrase(a+"h",font));
                        break;
                }
                cell.setFixedHeight(size);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                table.addCell(cell);
            }
        }
        return table;
    }

    private String getContent(String filePath){
        String res = "";
        try {
            res = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + filePath).getPath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
