package com.app;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Stock {
	private static Logger log = Logger.getLogger(Stock.class);
	private void runErrdata(){
		int i = 0;
		while(true){
			i++;
			List<Map> list = DBUtil.getList("select * from errdata", null);
			if(i>3 || list.isEmpty()){
				if(i>3)SimpleMailSender.sendHtmlMail("Stock Timer Error", "","84529527@qq.com");
				i=0;
				break;
			}
			for(Map map : list){
				try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
				String url = map.get("url")+"";
				String method = map.get("method")+"";
				String[] param = (map.get("param")+"").split(",");
				long id = (Long)map.get("id");
				
				if("getIndustry".equalsIgnoreCase(method)){
					JSONArray ja = this.getIndustry(url);
					if(ja!=null&&!ja.isEmpty()){
						this.deleteErrdata(id);
					}
				}else if("getDaily".equalsIgnoreCase(method)){
					JSONObject jo = this.getDaily(url, param[0], param[1]);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
					}
				}else if("getQuarterkuai".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterkuai(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateQuarterField(param[0]);
					}
				}else if("getQuarter".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarter(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateQuarterField(param[0]);
					}
				}else if("getQuarterfund".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterfund(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}else if("getQuarterQFII".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterQFII(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}else if("getQuartershebao".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuartershebao(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}else if("getQuarterquansh".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterquansh(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}else if("getQuarterbaoxian".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterbaoxian(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}else if("getQuarterxintuo".equalsIgnoreCase(method)){
					JSONObject jo = this.getQuarterxintuo(param[0], url);
					if(jo!=null && !jo.isEmpty()){
						this.deleteErrdata(id);
						this.updateZhuliField(param[0]);
					}
				}
			}
		}
	}
	private void deleteErrdata(long id){
		DBUtil.executeUpdateSql("delete from errdata where id=?", new Object[]{id});
	}
	//行业板块
	public void runIndustry(){
		String url = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C._BKHY&sty=FPGBKI&st=c&sr=-1&p=1&ps=5000&cb=&js=[(x)]&token=7bc05d0d4c3c22ef9fca8c2a912d779c&v=%s";
		url = String.format(url, System.currentTimeMillis()/10000000000000d);
		getIndustry(url);
	}

	private JSONArray getIndustry(String url) {
		JSONArray ja = null;
		try {
			URL q_url = new URL(url);
			Scanner scanner = new Scanner(q_url.openStream(),"utf-8");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			ja = parseJSONArray(str);
			for(Object obj : ja){
				String[] one = (obj+"").split(",");
				String cdate = String.format("%tF", new Date());
				String code = one[1];
				String name = one[2];
				Double close = parseDouble(one[18]);
				Double zdvalue = parseDouble(one[19]);
				Double zdrate = parseDouble(one[3]);
				Double amount = parseDouble(one[4]);
				Double huanrate = parseDouble(one[5]);
				Double znum = parseDouble(one[6].split("\\|")[0]);
				Double dnum = parseDouble(one[6].split("\\|")[2]);
				Map map = DBUtil.getObject("select * from industry where code=? and cdate=(select max(cdate) from industry where code=?)", new Object[]{code,code});
				Double c = parseDouble(map.get("close")+"");
				Double a = parseDouble(map.get("amount")+"");
				if(close!=null&&amount!=null&&(c==null||a==null||close.compareTo(c)!=0||amount.compareTo(a)!=0)){
					DBUtil.executeUpdateSql(
							"insert into industry(cdate,code,name,close,zdvalue,zdrate,amount,huanrate,znum,dnum) values(?,?,?,?,?,?,?,?,?,?)",
							new Object[] { cdate, code, name, close,zdvalue, zdrate, amount,huanrate, znum, dnum });
					
					//行业内个股
					String durl = "http://nufm.dfcfw.com/EM_Finance2014NumericApplication/JS.aspx?type=CT&cmd=C.%s&sty=FCOIATA&sortType=C&sortRule=-1&page=%s&pageSize=20&js={rank:[(x)],pages:(pc)}&token=7bc05d0d4c3c22ef9fca8c2a912d779c&jsName=quote_123&_g=%s";
					JSONObject jo = getDaily(String.format(durl, code+"1","1",System.currentTimeMillis()/10000000000000d),code,name);
					int pages = parseIntValue(jo,"pages");
					for(int i=2;i<=pages;i++){
						try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
						getDaily(String.format(durl, code+"1",i,System.currentTimeMillis()/10000000000000d),code,name);
					}
				}
			}
		} catch (Exception e) {
			log.error(url, e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method) values(?,?)",
					new Object[] { url,"getIndustry"});
		}
		runErrdata();
		return ja;
	}
	
	
	public void runQuarter() {
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		String ji = "";
		String saveji = "";
		if(month<3){
			ji = c.get(Calendar.YEAR)-1 + "-12-31";
			saveji = c.get(Calendar.YEAR)-1 + "-12-30";
		}else if(month<6){
			ji = c.get(Calendar.YEAR)-1 + "-12-31,"+c.get(Calendar.YEAR)+"-03-31";
			saveji = c.get(Calendar.YEAR)-1 + "-12-30,"+c.get(Calendar.YEAR)+"-03-30";
		}else if(month<9){
			ji = c.get(Calendar.YEAR) + "-06-30";
			saveji = ji;
		}else if(month<12){
			ji = c.get(Calendar.YEAR) + "-09-30";
			saveji = ji;
		}
		String[] aji = ji.split(",");
		String[] asaveji = saveji.split(",");
		for(int j=0;j<aji.length && !ji.isEmpty();j++){
			String jidu = aji[j];
			String savejidu = asaveji[j];
			
			getQuarterReport(jidu, savejidu);
			this.getQuarterZhuli(jidu, savejidu);
		}
	}

	public void getQuarterReport(String jidu, String savejidu) {
		//季报数据 业绩快报
		String qurl = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=SR&sty=YJKB&fd=%s&st=13&sr=-1&p=%s&ps=50&js={pages:(pc),data:[(x)]}&rt=%s";
		JSONObject jo = getQuarterkuai(savejidu,String.format(qurl, jidu,"1",System.currentTimeMillis()/30000));
		
		int pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterkuai(savejidu,String.format(qurl, jidu,i,System.currentTimeMillis()/30000));
		}
		//季报数据   业绩报表
		qurl = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=SR&sty=YJBB&fd=%s&st=13&sr=-1&p=%s&ps=50&js={pages:(pc),data:[(x)]}&stat=0&rt=%s";
		jo = getQuarter(savejidu, String.format(qurl, jidu,"1",System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarter(savejidu,String.format(qurl, jidu,i,System.currentTimeMillis()/30000));
		}
		runErrdata();
		updateQuarterField(savejidu);
	}
	private void updateQuarterField(String savejidu) {
		String update = "update quarter t1 join quarter t2 on t1.code=t2.code and t2.cdate=adddate(?,interval -12 month)"
				+ " set t1.epsrate = (t1.eps-t2.eps)/abs(t2.eps)*100 where t1.cdate=?";
		DBUtil.executeUpdateSql(update, new Object[]{savejidu,savejidu});
	}
	private JSONObject getQuarterkuai(String savejidu,String url) {
		JSONObject jo = null;
		try {
			URL q_url = new URL(url);
			Scanner scanner = new Scanner(q_url.openStream(),"utf-8");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof String) {
					String[] one = ((String) object).split(",");
					String code = one[0];
					String name = one[1];
					Double eps = parseDouble(one[2]);
					Double incomes = parseDouble(one[3]);
					Double incomesrate = parseDouble(one[5]);
					Double profits = parseDouble(one[7]);
					Double profitsrate = parseDouble(one[9]);
					Double naps = parseDouble(one[11]);
					Double roe = parseDouble(one[12]);
					int a = DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),name=?,eps=?,incomes=?,incomesrate=?,profits=?,profitsrate=?,naps=?,roe=? where cdate=? and code=?",
							new Object[] { name, eps, incomes,
									incomesrate, profits, profitsrate, naps,
									roe,savejidu, code });
					if(a<1){
						DBUtil.executeUpdateSql(
								"insert into quarter(cdate,code,name,eps,incomes,incomesrate,profits,profitsrate,naps,roe) values(?,?,?,?,?,?,?,?,?,?)",
								new Object[] { savejidu, code, name, eps, incomes,
										incomesrate, profits, profitsrate, naps,
										roe });
						
					}
				}
			}
		} catch (Exception e) {
			log.error(url,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { url,"getQuarterkuai",savejidu });
		}
		return jo;
	}
	private JSONObject getQuarter(String savejidu, String qurl) {
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"utf-8");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof String) {
					String[] one = ((String) object).split(",");
					String code = one[0];
					String name = one[1];
					Double eps = parseDouble(one[2]);
					Double incomes = parseDouble(one[4]);
					Double incomesrate = parseDouble(one[5]);
					Double profits = parseDouble(one[7]);
					Double profitsrate = parseDouble(one[8]);
					Double naps = parseDouble(one[10]);
					Double roe = parseDouble(one[11]);
					int a = DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),name=?,eps=?,incomes=?,incomesrate=?,profits=?,profitsrate=?,naps=?,roe=? where cdate=? and code=?",
							new Object[] { name, eps, incomes, incomesrate,
									profits, profitsrate, naps, roe,
									savejidu, code });
					if (a < 1) {
						DBUtil.executeUpdateSql(
								"insert into quarter(cdate,code,name,eps,incomes,incomesrate,profits,profitsrate,naps,roe) values(?,?,?,?,?,?,?,?,?,?)",
								new Object[] { savejidu, code, name, eps,
										incomes, incomesrate, profits,
										profitsrate, naps, roe });
					}
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarter",savejidu });
		}
		return jo;
	}
	
	public void getQuarterZhuli(String jidu, String savejidu) {
		//基金
		String qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=WZhDYzDv&stat=1&cmd=1&date=%s&rt=%s";
		JSONObject jo = getQuarterfund(savejidu, String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		int pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterfund(savejidu, String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		//QFII
		qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=kDtcyzBP&stat=2&cmd=1&date=%s&rt=%s";
		jo = getQuarterQFII(savejidu,String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterQFII(savejidu,String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		//社保
		qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=xzivGlGc&stat=3&cmd=1&date=%s&rt=%s";
		jo = getQuartershebao(savejidu,String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuartershebao(savejidu,String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		//券商
		qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=vaoDGFiY&stat=4&cmd=1&date=%s&rt=%s";
		jo = getQuarterquansh(savejidu,String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterquansh(savejidu,String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		//保险
		qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=ayhHUeQI&stat=5&cmd=1&date=%s&rt=%s";
		jo = getQuarterbaoxian(savejidu,String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterbaoxian(savejidu,String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		//信托
		qurl = "http://data.eastmoney.com/zlsj/zlsj_list.aspx?type=ajax&st=2&sr=-1&p=%s&ps=50&jsObj=mUebovVU&stat=6&cmd=1&date=%s&rt=%s";
		jo = getQuarterxintuo(savejidu,String.format(qurl, "1",jidu,System.currentTimeMillis()/30000));
		
		pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQuarterxintuo(savejidu,String.format(qurl, i,jidu,System.currentTimeMillis()/30000));
		}
		runErrdata();
		updateZhuliField(savejidu);
	}
	private void updateZhuliField(String savejidu) {
		String update = "update quarter set orgnum=ifnull(fundnum,0)+ifnull(qfiinum,0)+ifnull(shebaonum,0)+ifnull(quanshnum,0)+ifnull(baoxiannum,0)+ifnull(xintuonum,0)"
				+ ",orgamount=ifnull(fundamount,0)+ifnull(qfiiamount,0)+ifnull(shebaoamount,0)+ifnull(quanshamount,0)+ifnull(baoxianamount,0)+ifnull(xintuoamount,0)"
				+ ",orgzgbrate=ifnull(fundzgbrate,0)+ifnull(qfiizgbrate,0)+ifnull(shebaozgbrate,0)+ifnull(quanshzgbrate,0)+ifnull(baoxianzgbrate,0)+ifnull(xintuozgbrate,0)"
				+ ",orgltrate=ifnull(fundltrate,0)+ifnull(qfiiltrate,0)+ifnull(shebaoltrate,0)+ifnull(quanshltrate,0)+ifnull(baoxianltrate,0)+ifnull(xintuoltrate,0)"
				+ " where cdate=?";
		DBUtil.executeUpdateSql(update, new Object[]{savejidu});
	}

	private JSONObject getQuarterQFII(String savejidu, String qurl){
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double qfiinum = parseDouble(one.getString("ShareHDNum"));
					Double qfiiamount = parseDouble(one.getString("VPosition"));
					Double qfiizgbrate = parseDouble(one.getString("TabRate"));
					Double qfiiltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),qfiinum=?,qfiiamount=?,qfiizgbrate=?,qfiiltrate=? where cdate=? and code=?",
							new Object[] { qfiinum, qfiiamount, qfiizgbrate,
									qfiiltrate, savejidu, code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarterQFII",savejidu });
		}
		return jo;
	}
	private JSONObject getQuartershebao(String savejidu, String qurl){
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double shebaonum = parseDouble(one.getString("ShareHDNum"));
					Double shebaoamount = parseDouble(one
							.getString("VPosition"));
					Double shebaozgbrate = parseDouble(one.getString("TabRate"));
					Double shebaoltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),shebaonum=?,shebaoamount=?,shebaozgbrate=?,shebaoltrate=? where cdate=? and code=?",
							new Object[] { shebaonum, shebaoamount,
									shebaozgbrate, shebaoltrate, savejidu, code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuartershebao",savejidu });
		}
		return jo;
	}
	private JSONObject getQuarterquansh(String savejidu, String qurl) {
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double quanshnum = parseDouble(one.getString("ShareHDNum"));
					Double quanshamount = parseDouble(one
							.getString("VPosition"));
					Double quanshzgbrate = parseDouble(one.getString("TabRate"));
					Double quanshltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),quanshnum=?,quanshamount=?,quanshzgbrate=?,quanshltrate=? where cdate=? and code=?",
							new Object[] { quanshnum, quanshamount,
									quanshzgbrate, quanshltrate, savejidu, code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarterquansh",savejidu });
		}
		return jo;
	}
	private JSONObject getQuarterfund(String savejidu, String qurl){
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double fundnum = parseDouble(one.getString("ShareHDNum"));
					Double fundamount = parseDouble(one.getString("VPosition"));
					Double fundzgbrate = parseDouble(one.getString("TabRate"));
					Double fundltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),fundnum=?,fundamount=?,fundzgbrate=?,fundltrate=? where cdate=? and code=?",
							new Object[] { fundnum, fundamount, fundzgbrate,
									fundltrate, savejidu, code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarterfund",savejidu });
		}
		return jo;
	}
	private JSONObject getQuarterbaoxian(String savejidu, String qurl){
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double baoxiannum = parseDouble(one.getString("ShareHDNum"));
					Double baoxianamount = parseDouble(one
							.getString("VPosition"));
					Double baoxianzgbrate = parseDouble(one
							.getString("TabRate"));
					Double baoxianltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),baoxiannum=?,baoxianamount=?,baoxianzgbrate=?,baoxianltrate=? where cdate=? and code=?",
							new Object[] { baoxiannum, baoxianamount,
									baoxianzgbrate, baoxianltrate, savejidu,
									code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarterbaoxian",savejidu });
		}
		return jo;
	}
	private JSONObject getQuarterxintuo(String savejidu, String qurl) {
		JSONObject jo = null;
		try {
			URL q_url = new URL(qurl);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					Double xintuonum = parseDouble(one.getString("ShareHDNum"));
					Double xintuoamount = parseDouble(one
							.getString("VPosition"));
					Double xintuozgbrate = parseDouble(one.getString("TabRate"));
					Double xintuoltrate = parseDouble(one.getString("LTZB"));
					DBUtil.executeUpdateSql(
							"update quarter set ctime=sysdate(),xintuonum=?,xintuoamount=?,xintuozgbrate=?,xintuoltrate=? where cdate=? and code=?",
							new Object[] { xintuonum, xintuoamount,
									xintuozgbrate, xintuoltrate, savejidu, code });
				}
			}
		} catch (Exception e) {
			log.error(qurl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { qurl,"getQuarterxintuo",savejidu });
		}
		return jo;
	}
	public void oneQuarter(){
		//季报数据   业绩报表
		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.DECEMBER,31);
		
		Calendar e = Calendar.getInstance();
		e.set(2017, Calendar.JUNE,30);
		
		while(true){
			if(c.getTimeInMillis()<=e.getTimeInMillis()){
				String jidu = String.format("%tF", c.getTime());
				c.set(Calendar.DAY_OF_MONTH, 30);
				String savejidu = String.format("%tF", c.getTime());
				
				getQuarterReport(jidu, savejidu);
				this.getQuarterZhuli(jidu, savejidu);
				
				c.add(Calendar.MONTH, 3);
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			}else{
				break;
			}
		}
		
	}
	
	private JSONObject getDaily(String durl,String bkid,String bkname) {
		JSONObject jo = null;
		try {
			URL d_url = new URL(durl);
			Scanner scanner = new Scanner(d_url.openStream(),"utf-8");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "rank");
			for (Object object : ja) {
				if (object instanceof String) {
					String[] one = ((String) object).split(",");
					String cdate = String.format("%tF", new Date());
					String code = one[1];
					String name = one[2];
					Double close = parseDouble(one[3]);
					Double zdrate = parseDouble(one[5].replaceAll("%", ""));
					Double closepre = parseDouble(one[9]);
					Double open = parseDouble(one[10]);
					Double high = parseDouble(one[11]);
					Double low = parseDouble(one[12]);
					Double vol = parseDouble(one[7]);
					Double amount = parseDouble(one[8]);
					Map map = DBUtil.getObject("select * from daily where code=? and cdate=(select max(cdate) from daily where code=?)", new Object[]{code,code});
					Double c = parseDouble(map.get("close")+"");
					Double a = parseDouble(map.get("amount")+"");
					if(close!=null&&amount!=null&&(c==null||a==null||close.compareTo(c)!=0||amount.compareTo(a)!=0)){
						
						DBUtil.executeUpdateSql(
								"insert into daily(cdate,code,name,bkid,bkname,close,zdrate,closepre,open,high,low,vol,amount) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
								new Object[] { cdate, code, name,bkid,bkname, close,zdrate, closepre,
										open, high, low, vol, amount });
					}
				}
			}
		} catch (Exception e) {
			log.error(durl,e);
			DBUtil.executeUpdateSql(
					"insert into errdata(url,method,param) values(?,?,?)",
					new Object[] { durl,"getDaily",bkid+","+bkname });
		}
		return jo;
	}
	
	public void runLonghubang(){
		Calendar c = Calendar.getInstance();
		String e = String.format("%tF", c.getTime());
		c.add(Calendar.YEAR, -1);
		String s = String.format("%tF", c.getTime());
		
		String url = "http://data.eastmoney.com/DataCenter_V3/stock2016/StockStatistic/pagesize=50,page=%s,sortRule=-1,sortType=,startDate=%s,endDate=%s,gpfw=0,js=data_tab_3.html?rt=%s";
		JSONObject jo = getLonghubang(String.format(url, "1",s,e,System.currentTimeMillis()/30000));
		
		int pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception ex){};
			getLonghubang(String.format(url, i,s,e,System.currentTimeMillis()/30000));
		}
	}

	private JSONObject getLonghubang(String url){
		JSONObject jo = null;
		try {
			URL q_url = new URL(url);
			Scanner scanner = new Scanner(q_url.openStream(),"gbk");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			str = str.substring(str.indexOf("{"));
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof JSONObject) {
					JSONObject one = (JSONObject) object;
					String code = one.getString("SCode");
					String name = one.getString("SName");
					Double zhang1yrate = parseDouble(one.getString("Rchange1y"));
					Double zhang6mrate = parseDouble(one.getString("Rchange6m"));
					Double zhang3mrate = parseDouble(one.getString("Rchange3m"));
					Double zhang1mrate = parseDouble(one.getString("Rchange1m"));
					int a = DBUtil
							.executeUpdateSql(
									"update qianguqianping set ctime=sysdate(),zhang1yrate=?,zhang6mrate=?,zhang3mrate=?,zhang1mrate=? where code=?",
									new Object[] { zhang1yrate, zhang6mrate,
											zhang3mrate, zhang1mrate, code });
					if (a < 1) {
						DBUtil.executeUpdateSql(
								"insert into qianguqianping (code,name,zhang1yrate,zhang6mrate,zhang3mrate,zhang1mrate) values (?,?,?,?,?,?)",
								new Object[] { code, name, zhang1yrate,
										zhang6mrate, zhang3mrate, zhang1mrate });
					}
				}
			}
		} catch (Exception e) {
			log.error(url,e);
		}
		return jo;
	}
	public void runQianguqianping() {
		String url = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=FD&sty=TSTC&st=1&sr=1&p=%s&ps=50&js=(x)&mkt=0&rt=%s";
		JSONObject jo = getQianguqianping(String.format(url, "1",System.currentTimeMillis()/30000));
		
		int pages = parseIntValue(jo,"pages");
		for(int i=2;i<=pages;i++){
			try{Thread.sleep(new Random().nextInt(1000));}catch(Exception e){};
			getQianguqianping(String.format(url, i,System.currentTimeMillis()/30000));
		}
	}
	private JSONObject getQianguqianping(String url) {
		JSONObject jo = null;
		try {
			URL q_url = new URL(url);
			Scanner scanner = new Scanner(q_url.openStream(),"utf-8");
			String str = "";
			while(scanner.hasNextLine()){
				str += scanner.nextLine();
			}
			scanner.close();
			jo = parseJSONObject(str);
			JSONArray ja = getJSONArray(jo, "data");
			for (Object object : ja) {
				if (object instanceof String) {
					String[] one = ((String) object).split(",");
					//"000622,*ST恒立,2,短线止升回落&sbquo;高抛。,8.44,0.24%,0.28,-,8.48,5.52"
					String code = one[0];
					String name = one[1];
					String suggest = one[3];
					Double close = parseDouble(one[4]);
					String zhangrate = one[5];
					Double huanrate = parseDouble(one[6]);
					Double pe = parseDouble(one[7]);
					Double zhucost = parseDouble(one[8]);
					Double orgrate = parseDouble(one[9]);
					int a = DBUtil
							.executeUpdateSql(
									"update qianguqianping set ctime=sysdate(),name=?,suggest=?,close=?,zhangrate=?,huanrate=?,pe=?,zhucost=?,orgrate=? where code=?",
									new Object[] { name, suggest, close,
											zhangrate, huanrate, pe, zhucost,
											orgrate, code });
					if (a < 1) {
						DBUtil.executeUpdateSql(
								"insert into qianguqianping (code,name,suggest,close,zhangrate,huanrate,pe,zhucost,orgrate) values (?,?,?,?,?,?,?,?,?)",
								new Object[] { code, name, suggest, close,
										zhangrate, huanrate, pe, zhucost,
										orgrate });
					}
				}
			}
		} catch (Exception e) {
			log.error(url, e);
		}
		return jo;
	}
	public Double parseDouble(String s){
		try{
			Double d = Double.valueOf(s);
			return d;
		}catch(Exception e){
			return null;
		}
	}
	public static JSONObject parseJSONObject(String str){
		try {
			return JSON.parseObject(str);
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	public JSONArray parseJSONArray(String str){
		try {
			return JSON.parseArray(str);
		} catch (Exception e) {
			return new JSONArray();
		}
	}
	public static JSONArray getJSONArray(JSONObject jo,String key){
		try {
			return jo.getJSONArray(key);
		} catch (Exception e) {
			return new JSONArray();
		}
	}
	public static int parseIntValue(JSONObject jo,String key){
		try {
			return jo.getIntValue(key);
		} catch (Exception e) {
			return 0;
		}
	}
	
	private void sql(){
		/*
		select t1.cdate,t1.code,t1.name,t1.epsrate,t2.epsrate,t3.epsrate,t4.epsrate,t1.incomesrate,t2.incomesrate,t3.incomesrate,t4.incomesrate
		,t1.orgltrate,t2.orgltrate,t3.orgltrate,t4.orgltrate		
		,t5.suggest,t5.zhucost,t5.orgrate
		from quarter t1 
		join quarter t2 on t1.code=t2.code and t2.cdate=adddate((select adddate(max(cdate),interval 0 month) from quarter),interval -3 month)
		join quarter t3 on t1.code=t3.code and t3.cdate=adddate((select adddate(max(cdate),interval 0 month) from quarter),interval -6 month)
		join quarter t4 on t1.code=t4.code and t4.cdate=adddate((select adddate(max(cdate),interval 0 month) from quarter),interval -9 month)
		join qianguqianping t5 on t1.code=t5.code
		where t1.cdate=(select adddate(max(cdate),interval 0 month) from quarter) 
		and t1.epsrate>20 and t2.epsrate>20 and t3.epsrate>20 
		and t1.incomesrate>20 and t2.incomesrate>20 and t3.incomesrate>20 
		order by t1.epsrate,t2.epsrate,t3.epsrate
		
		CREATE TABLE quarter (
		  cdate date NOT NULL,
		  code varchar(6) NOT NULL,
		  name varchar(20) DEFAULT NULL,
		  eps decimal(20,4) DEFAULT NULL COMMENT '每股收益',
		  incomes decimal(20,2) DEFAULT NULL COMMENT '营业收入',
		  incomesrate decimal(20,2) DEFAULT NULL COMMENT '营业收入同比%',
		  profits decimal(20,2) DEFAULT NULL COMMENT '净利润',
		  profitsrate decimal(20,2) DEFAULT NULL COMMENT '净利润同比%',
		  naps decimal(20,2) DEFAULT NULL COMMENT '每股净资产',
		  roe decimal(20,2) DEFAULT NULL COMMENT '净资产收益率',
		  epsrate decimal(20,2) DEFAULT NULL COMMENT '每股收益同比%',
		  ctime datetime DEFAULT CURRENT_TIMESTAMP,
		  fundnum decimal(20,2) DEFAULT NULL COMMENT '基金持股数',
		  fundamount decimal(20,2) DEFAULT NULL COMMENT '基金持股市值',
		  fundzgbrate decimal(12,8) DEFAULT NULL COMMENT '基金持股占总股本比%',
		  fundltrate decimal(12,8) DEFAULT NULL COMMENT '基金持股占流通股比%',
		  qfiinum decimal(20,2) DEFAULT NULL COMMENT 'QFII持股数',
		  qfiiamount decimal(20,2) DEFAULT NULL COMMENT 'QFII持股市值',
		  qfiizgbrate decimal(12,8) DEFAULT NULL COMMENT 'QFII持股占总股本比%',
		  qfiiltrate decimal(12,8) DEFAULT NULL COMMENT 'QFII持股占流通股比%',
		  shebaonum decimal(20,2) DEFAULT NULL COMMENT '社保持股数',
		  shebaoamount decimal(20,2) DEFAULT NULL COMMENT '社保持股市值',
		  shebaozgbrate decimal(12,8) DEFAULT NULL COMMENT '社保持股占总股本比%',
		  shebaoltrate decimal(12,8) DEFAULT NULL COMMENT '社保持股占流通股比%',
		  quanshnum decimal(20,2) DEFAULT NULL COMMENT '券商持股数',
		  quanshamount decimal(20,2) DEFAULT NULL COMMENT '券商持股市值',
		  quanshzgbrate decimal(12,8) DEFAULT NULL COMMENT '券商持股占总股本比%',
		  quanshltrate decimal(12,8) DEFAULT NULL COMMENT '券商持股占流通股比%',
		  baoxiannum decimal(20,2) DEFAULT NULL COMMENT '保险持股数',
		  baoxianamount decimal(20,2) DEFAULT NULL COMMENT '保险持股市值',
		  baoxianzgbrate decimal(12,8) DEFAULT NULL COMMENT '保险持股占总股本比%',
		  baoxianltrate decimal(12,8) DEFAULT NULL COMMENT '保险持股占流通股比%',
		  xintuonum decimal(20,2) DEFAULT NULL COMMENT '信托持股数',
		  xintuoamount decimal(20,2) DEFAULT NULL COMMENT '信托持股市值',
		  xintuozgbrate decimal(12,8) DEFAULT NULL COMMENT '信托持股占总股本比%',
		  xintuoltrate decimal(12,8) DEFAULT NULL COMMENT '信托持股占流通股比%',
		  orgnum decimal(20,2) DEFAULT NULL COMMENT '总持股数',
		  orgamount decimal(20,2) DEFAULT NULL COMMENT '总持股市值',
		  orgzgbrate decimal(12,8) DEFAULT NULL COMMENT '总持股占总股本比%',
		  orgltrate decimal(12,8) DEFAULT NULL COMMENT '总持股占流通股比%',
		  PRIMARY KEY (cdate,code)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8
			
		CREATE TABLE daily (
		  cdate date NOT NULL,
		  code varchar(6) NOT NULL,
		  name varchar(20) DEFAULT NULL,
		  bkid varchar(6) NOT NULL,
		  bkname varchar(20) NOT NULL,
		  close decimal(20,2) DEFAULT NULL COMMENT '收盘',
		  zdrate decimal(20,2) DEFAULT NULL COMMENT '涨跌幅',
		  closepre decimal(20,2) DEFAULT NULL COMMENT '昨收',
		  open decimal(20,2) DEFAULT NULL COMMENT '开盘',
		  high decimal(20,2) DEFAULT NULL COMMENT '最高',
		  low decimal(20,2) DEFAULT NULL COMMENT '最低',
		  vol decimal(20,2) DEFAULT NULL COMMENT '成交量',
		  amount decimal(20,2) DEFAULT NULL COMMENT '成交额',
		  ctime datetime DEFAULT CURRENT_TIMESTAMP,
		  PRIMARY KEY (cdate,code)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8
		
		CREATE TABLE industry (
		  cdate date NOT NULL,
		  code varchar(6) NOT NULL,
		  name varchar(20) DEFAULT NULL,
		  close decimal(20,2) DEFAULT NULL COMMENT '行业收盘价',
		  zdvalue decimal(20,2) DEFAULT NULL COMMENT '涨跌额',
		  zdrate decimal(20,2) DEFAULT NULL COMMENT '涨跌幅',
		  amount decimal(20,2) DEFAULT NULL COMMENT '总市值',
		  huanrate decimal(20,2) DEFAULT NULL COMMENT '换手率',
		  znum decimal(20,2) DEFAULT NULL COMMENT '上涨家数',
		  dnum decimal(20,2) DEFAULT NULL COMMENT '下跌家数',
		  ctime datetime DEFAULT CURRENT_TIMESTAMP,
		  PRIMARY KEY (cdate,code)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8
		
		create table errdata(
		  id int UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
		  ctime datetime DEFAULT CURRENT_TIMESTAMP,
		  url varchar(500) not null,
		  method varchar(20) COMMENT 'url对应的方法名 参数以逗号隔开保存在param字段',
		  param varchar(500)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8
		
		CREATE TABLE qianguqianping (
		  code varchar(6) NOT NULL,
		  name varchar(20) DEFAULT NULL,
		  suggest varchar(200) DEFAULT NULL COMMENT '意见',
		  close decimal(20,2) DEFAULT NULL COMMENT '收盘',
		  zhangrate varchar(10) DEFAULT NULL COMMENT '涨跌幅%',
		  huanrate decimal(20,2) DEFAULT NULL COMMENT '换手率%',
		  pe decimal(20,2) DEFAULT NULL COMMENT '市盈率%',
		  zhucost decimal(20,2) DEFAULT NULL COMMENT '主力成本',
		  orgrate decimal(20,2) DEFAULT NULL COMMENT '机构参与度%',
		  ctime datetime DEFAULT CURRENT_TIMESTAMP,
		  zhang1mrate decimal(7,2) DEFAULT NULL COMMENT '近1月涨幅%',
		  zhang3mrate decimal(7,2) DEFAULT NULL COMMENT '近3月涨幅%',
		  zhang6mrate decimal(7,2) DEFAULT NULL COMMENT '近6月涨幅%',
		  zhang1yrate decimal(7,2) DEFAULT NULL COMMENT '近1年涨幅%',
		  PRIMARY KEY (code)
		) ENGINE=InnoDB DEFAULT CHARSET=utf8
	 */
	}
}
