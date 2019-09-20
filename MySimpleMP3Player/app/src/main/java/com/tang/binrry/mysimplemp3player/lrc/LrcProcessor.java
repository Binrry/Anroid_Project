package com.tang.binrry.mysimplemp3player.lrc;

import android.util.Log;

import com.tang.binrry.mysimplemp3player.beans.LrcBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by User on 2019/6/10.
 */

public class LrcProcessor {
    ArrayList<LrcBean>lrcmap=new ArrayList<LrcBean>();

    public static String getCharSet(InputStream in)
    {
        byte[] b=new byte[3];
        String charset="";
        try {
            in.read(b);
            in.close();
            if(b[0]==(byte)0xEF&&b[1]==(byte)0xBB&&b[2]==(byte)0xBF)
                charset="UTF-8";
            else if(b[0]==(byte)0xFE&&b[1]==(byte)0xFF)
                charset="UTF-16BE";
            else if(b[0]==(byte)0xFF&&b[1]==(byte)0xFE)
                charset="UTF-16LE";
            else
                charset="GBK";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset;
    }

    public ArrayList<LrcBean> process(InputStream in, String charset)
    {
        try {
            InputStreamReader inreader;
            if(!charset.equals(""))
            {
                inreader=new InputStreamReader(in,charset);
            }else
            {
                inreader=new InputStreamReader(in);
            }
            BufferedReader br=new BufferedReader(inreader);
            String temp;
            while ((temp=br.readLine())!=null)
            {

                paraseLine(temp);
            }
           Collections.sort(lrcmap);

            br.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lrcmap;
    }

    private void paraseLine(String str)
    {
        String msg;
        if(str.startsWith("[ti:"))
        {
            String title=str.substring(4,str.length()-1);
            System.out.println("title------>"+title);
        }
        else if(str.startsWith("[ar:"))
        {
            String singer=str.substring(4,str.length()-1);
            System.out.println("singer----->"+singer);
        }else if(str.startsWith("[al:"))
        {
            String album=str.substring(4,str.length()-1);
            System.out.println("album------>"+album);
        }else
        {
            Pattern p=Pattern.compile("\\[\\s*[0-9]{1,2}\\s*:\\s*[0-5][0-9]\\s*[\\.:]?\\s*[0-9]?[0-9]?\\s*\\]");
            Matcher m=p.matcher(str);
            msg=str.substring(str.lastIndexOf("]")+1);
            while (m.find())
            {
                String timestr=m.group();

                timestr=timestr.substring(1,timestr.length()-1);
                Log.i("test",timestr);
                int timeMil=time2long(timestr);
                LrcBean temp=new LrcBean(timeMil,msg);
                lrcmap.add(temp);
                Log.i("Test",timeMil+"-----"+msg);
            }
        }
    }

    private int time2long(String timestr)
    {
        int min=0,sec=0,mil=0;
        try {
            timestr=timestr.replace(".",":");
            String[] s=timestr.split(":");
            switch (s.length)
            {
                case 2:
                {
                    min=Integer.parseInt(s[0]);
                    sec=Integer.parseInt(s[1]);
                    break;
                }
                case 3:
                {
                    min=Integer.parseInt(s[0]);
                    sec=Integer.parseInt(s[1]);
                    mil=Integer.parseInt(s[2]);
                    break;
                }
                default:
                    break;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return min*60*1000+sec*1000+mil*10;
    }


}
