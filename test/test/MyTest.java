package test;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.app.DBUtil;
import com.app.SimpleMailSender;
import com.app.Stock;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class MyTest {
	@Test
	public void test8(){
		final WebClient webClient = new WebClient(BrowserVersion.CHROME);//新建一个模拟谷歌Chrome浏览器的浏览器客户端对象
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setDownloadImages(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
		webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX
		
		HtmlPage loginPage=null;//尝试加载上面图片例子给出的网页
		for(int i=0;i<20;i++){
			try {
				loginPage = webClient
						.getPage("https://bch.huaaiangel.com/bchwx/index.php?r=site%2Flogin");
				webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
				System.out.println("loginPage:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
				if(loginPage!=null)break;
			} catch (Exception e) {}
		}
		for(int i=0;i<20;i++){
			try {
				HtmlForm form = loginPage.getForms().get(0);
				form.getInputByName("PasswdLoginForm[loginid]").setValueAttribute(
						"18600369418");
				form.getInputByName("PasswdLoginForm[password]").setValueAttribute(
						"123456");
				HtmlPage page2 = form.getButtonByName("login-button").click();
				webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
				System.out.println("login:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
				if(page2!=null)break;
			} catch (Exception e) {}
		}
		
		while(true){
			Calendar c = Calendar.getInstance();
			long l = c.getTimeInMillis();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			if(hour<8) c.set(Calendar.HOUR_OF_DAY, 7);
			else c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 54);
			c.set(Calendar.SECOND, 0);
			long b = c.getTimeInMillis();
			
			c.set(Calendar.MINUTE, 57);
			long b2 = c.getTimeInMillis();
			
//			if((hour==0 || hour==8) && minute<12){
//				try {Thread.sleep(2 * 1000);} catch (Exception e) {}
//			}else if(l<b){
//				try {Thread.sleep(5 * 60 * 1000);} catch (Exception e) {}
//			} else if(l<b2){
//				try {Thread.sleep(2 * 60 * 1000);} catch (Exception e) {}
//			}else{
//				try {Thread.sleep(2 * 1000);} catch (Exception e) {}
//			}
			
//			HtmlElement notpay = null;
//	        try {
//				HtmlPage order = webClient
//						.getPage("https://bch.huaaiangel.com/bchwx/index.php?r=my/orders");
//				webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
//				notpay = order.getDocumentElement().getOneHtmlElementByAttribute("span", "style", "color:red");
//			} catch (Exception e) {}
	        
//	        if(notpay==null){
	        	DomElement date = null;
	        	DomElement date2 = null;
	        	for(int i=0;i<20;i++){
	        		try {
	        			HtmlPage page3 = webClient
	        					.getPage("https://bch.huaaiangel.com/bchwx/index.php?r=pool%2Findex&deptId=23");//1,23
	        			webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
	        			System.out.println("keShiClick:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
	        			date = page3.getElementById("v2018-09-24");
//	        			date2 = page3.getElesmentById("v2018-09-14");
	        			if(date!=null)break;
	        		} catch (Exception e) {}
	        	}
	        	if(date==null){
	        		test8();
	        	}
	        	
	        	HtmlElement yuYueButton = null;
	        	for(int i=0;i<20;i++){
	        		try {
	        			HtmlPage page4 = date.click();
	        			webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
	        			System.out.println("dateClick:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
	        			System.out.println(page4.asXml());
	        			List<String> listName = new ArrayList<String>();
	        			List<HtmlElement> listYuYue = new ArrayList<HtmlElement>();
	        			//DomNodeList<HtmlElement> helist = page4.getElementById("spec_list").getElementsByTagName("a");
	        			List<HtmlElement> tempa = page4.getDocumentElement().getElementsByAttribute("a", "class", "weui-btn weui-btn_mini weui-btn_primary");
	        			for(HtmlElement he : tempa){
//	        				if("weui-btn weui-btn_mini weui-btn_primary".equalsIgnoreCase(he.getAttribute("class"))){
	        					try {
									String name = he.getParentNode()
													.getParentNode()
													.getParentNode()
													.getParentNode()
													.getPreviousElementSibling()
													.getElementsByTagName("h4")
													.get(0)
													.getTextContent();
									if(!name.contains("特需小夜")&&!name.contains("先天")){
										listName.add(name);
										listYuYue.add(he);
									}
								} catch (Exception e) {}
//	        				}
	        			}
	        			for(int j=0;j<listName.size();j++){
	        				if(listName.get(j).contains("知名专家")){
	        					yuYueButton = listYuYue.get(j);
	        					break;
	        				}
	        			}
	        			if(yuYueButton==null){
	        				for(int j=0;j<listName.size();j++){
		        				if(listName.get(j).contains("主任医师")&&!listName.get(j).contains("副主任医师")){
		        					yuYueButton = listYuYue.get(j);
		        					break;
		        				}
		        			}
	        			}
	        			if(yuYueButton==null){
	        				for(int j=0;j<listName.size();j++){
		        				if(listName.get(j).contains("副主任医师")){
		        					yuYueButton = listYuYue.get(j);
		        					break;
		        				}
		        			}
	        			}
	        			if(yuYueButton==null){
	        				for(int j=0;j<listName.size();j++){
		        				if(listName.get(j).contains("王强")){
		        					yuYueButton = listYuYue.get(j);
		        					break;
		        				}
		        			}
	        			}
	        			if(yuYueButton==null){
	        				for(int j=0;j<listName.size();j++){
		        				if(listName.get(j).length()<4){
		        					yuYueButton = listYuYue.get(j);
		        					break;
		        				}
		        			}
	        			}
	        			if(yuYueButton==null){
	        				for(int j=0;j<listName.size();j++){
		        				if(listName.get(j).contains("创伤骨科")){
		        					yuYueButton = listYuYue.get(j);
		        					break;
		        				}
		        			}
	        			}
	        			if(yuYueButton==null){
	        				yuYueButton = listYuYue.size()!=0?listYuYue.get(0):null;
	        			}
	        			if(yuYueButton!=null)break;
	        		} catch (Exception e) {}
	        	}
	        	HtmlPage page5 = null;
	        	
	        	for(int i=0;i<20;i++){
	        		try {
	        			page5 = yuYueButton.click();
	        			webClient.waitForBackgroundJavaScript(5000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
	        			System.out.println("yuYueClick:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
	        			if(page5!=null)break;
	        		} catch (Exception e) {}
	        	}
	        	try {
	        		HtmlForm queRenForm = page5.getForms().get(0);
	        		HtmlButton queRen = queRenForm.getOneHtmlElementByAttribute("button",
	        				"class", "btn btn-primary");
	        		System.out.println(queRenForm.asXml());
					HtmlPage page6 = queRen.click();
					webClient.waitForBackgroundJavaScript(10000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
					System.out.println("queRenClick:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:S").format(new Date()));
					SimpleMailSender.sendHtmlMail("挂号成功", "", "84529527@qq.com");
					break;
	        	} catch (Exception e) {}
//	        }
		}
		webClient.close();
		
	}
	@Test
	public void test7() throws Exception{
//		ScriptEngineManager manager = new ScriptEngineManager();   
//		ScriptEngine engine = manager.getEngineByName("javascript");
//		FileReader reader = new FileReader("b.js");   // 执行指定脚本   
//		engine.eval(reader);
//		Invocable invoke = (Invocable)engine;
//        String boo=(String)invoke.invokeFunction("C","1^^vd","73a2a68c71");
//        System.out.println("boo="+boo);
//		reader.close();
		
		//打开cookie管理
				CookieManager manager = new CookieManager();
				CookieHandler.setDefault(manager);
		
//		String json = Jsoup.connect("https://wechat.benmu-health.com/mobile/wx/conf/getWxConf?url=https:%2F%2Fwechat.benmu-health.com%2FwechatV2%2F")
//				.userAgent("Mozilla/5.0 (Linux; Android 8.0; FRD-AL00 Build/HUAWEIFRD-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044208 Mobile Safari/537.36 MicroMessenger/6.7.2.1340(0x2607023A) NetType/WIFI Language/zh_CN")
//				.cookie("__jsluid", "4e9fcf17604679b850400eb9d8eaaea1")
//				.cookie("_attention", "1")
//				.cookie("_ucp", "tesPZrlymQccF9U4HUbUTznCF4VQ7Ao9Ck74bPvfKQJaS-z0s_AoHeAHAIOqBpL3zxD-8Q..")
//				.cookie("bm_session_tm", "1537415285489")
//				.cookie("bm_session", "WVyqZXI7HwzQndOVPRuiEKjrD93lF75l_5124717")
//				.ignoreContentType(true)
//				.execute().body();
		
		String json = Jsoup.connect("https://wechat.benmu-health.com/mobile/wx/user/account?_=1537415573823")
				.userAgent("Mozilla/5.0 (Linux; Android 8.0; FRD-AL00 Build/HUAWEIFRD-AL00; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 MQQBrowser/6.2 TBS/044208 Mobile Safari/537.36 MicroMessenger/6.7.2.1340(0x2607023A) NetType/WIFI Language/zh_CN")
				.cookie("__jsluid", "4e9fcf17604679b850400eb9d8eaaea1")
				.cookie("_attention", "1")
				.cookie("_ucp", "tesPZrlymQccF9U4HUbUTznCF4VQ7Ao9Ck74bPvfKQJaS-z0s_AoHeAHAIOqBpL3zxD-8Q..")
				.cookie("bm_session_tm", "1537415285489")
				.cookie("bm_session", "WVyqZXI7HwzQndOVPRuiEKjrD93lF75l_5124717")
				.ignoreContentType(true)
				.execute().body();
		
		System.out.println(json);
	}
	@Test
	public void test6() throws Exception{
		String s = "<div class=\"spacing\"></div><center><img src=\"http://zzfws.bjjs.gov.cn:80/enroll/resources/enroll/CSS/images/img_dailog_enrollnone.jpg\"/></center><hr>";
		String re = s;
		Element date = Jsoup.parse(null)
				.select("th:contains(开始时间)")
				.next()
				.first();
		String d = date.html();
		SimpleMailSender.gycq();
		
	}
	@Test
	public void test5() throws Exception{
		//打开cookie管理
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		//登录页
		Document loginDoc = Jsoup.connect("https://www.thankfund.com/login")
								.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
								.get();
		//登录动作
		String loginRes = Jsoup.connect("https://www.thankfund.com/login_mobile")
							.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
							.referrer("https://www.thankfund.com/login")
							.data("mobile","18600369418","password","Mdffxwwq9988","captcha","")
							.ignoreContentType(true)
							.method(Method.POST)
							.execute()
							.body();
		JSONObject loginResJson = JSON.parseObject(loginRes);
		if(200==loginResJson.getIntValue("status")){
			JSONObject datas = JSON.parseObject(loginResJson.getString("datas"));
			JSONObject user = datas.getJSONObject("user");
			String fund_token = datas.getString("token");
			JSONObject fundUser = new JSONObject();
			fundUser.put("role_type", datas.getString("role_type"));
			fundUser.put("user_id", datas.getString("user_id"));
			fundUser.put("mobile", user.getString("mobile"));
			fundUser.put("auth_state", user.getString("auth_state"));
			fundUser.put("has_pay_password", user.getIntValue("trade_password_state"));
			fundUser.put("has_bank_card_no", user.getIntValue("bankcard_bound_state"));
			fundUser.put("invite_code", user.getString("customer_no"));
			
			Map extCookie = new HashMap();
			extCookie.put("fund_token",fund_token);
			extCookie.put("fund_captcha_show","false");
			extCookie.put("fund_user",URLEncoder.encode(fundUser.toJSONString(), "UTF-8"));
			//我的账户
			Document myAccountDoc = Jsoup.connect("https://www.thankfund.com/my_account")
										.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
										.referrer("https://www.thankfund.com/login")
										.cookies(extCookie)
										.get();
			
			//收益
			String shouyiRes = Jsoup.connect("https://www.thankfund.com/my_account/earning/list")
								.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
								.data("end_time","2018-09-30","funds_type","3","limit","90","page","1","start_time","2018-09-01")
								.cookies(extCookie)
								.ignoreContentType(true)
								.method(Method.POST)
								.execute()
								.body();
			JSONObject shouyiJson = JSON.parseObject(shouyiRes);
			int shouyiamount = 0;
			if(shouyiJson!=null){
				JSONArray records = shouyiJson.getJSONArray("datas");
				if(records!=null){
					for(int i=0;i<records.size();i++){
						shouyiamount += records.getJSONObject(i).getIntValue("amount");
					}
				}
			}
			
			SimpleMailSender.sendHtmlMail("我的账户", myAccountDoc.toString(),"84529527@qq.com");
			int balance = Double.valueOf(myAccountDoc.selectFirst("span:contains(账户余额)").selectFirst("strong").html().replaceAll("[, ]", "")).intValue();
			if(balance>=100){
				//提交余额购买
				String buyRes = Jsoup.connect("https://www.thankfund.com/invest/balanceBuyProducts")
									.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
									.referrer("https://www.thankfund.com/invest/detail/683068a8602c474a8d1746549a211b51")
									.cookies(extCookie)
									.data("amount",balance+"00")
									.data("customer_id",fundUser.getString("user_id"))
									.data("pay_amount",balance+"00")
									.data("product_id","683068a8602c474a8d1746549a211b51")
									.ignoreContentType(true)
									.method(Method.POST)
									.execute()
									.body();
				if(200!=JSON.parseObject(buyRes).getIntValue("status")){
					System.out.println(buyRes);
				}
			}
			
			String logoutRes = Jsoup.connect("https://www.thankfund.com/logout")
									.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
									.referrer("https://www.thankfund.com/my_account")
									.cookies(extCookie)
									.ignoreContentType(true)
									.method(Method.POST)
									.execute()
									.body();
			if(200!=JSON.parseObject(logoutRes).getIntValue("status")){
				System.out.println(logoutRes);
			}
		}else{
			System.out.println(loginRes);
		}
	}
	/**
	 * @throws Exception
	 */
	@Test
	public void test4() throws Exception{
		Document doc = Jsoup.connect("http://hdfgj.bjhd.gov.cn/xxgk/xxgs/zdly/zbgs/")
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
				.get();
		Element ul = doc.selectFirst("ul.list");
		Element span = ul.selectFirst("span");
		String date = span.html().replaceAll("[\\[ \\]]", "");
		System.out.println(ul.html());
		String res = Jsoup.connect("http://zzfws.bjjs.gov.cn/enroll/dyn/enroll/viewEnrollHomePager.json")
				.requestBody("{\"currPage\":1,\"pageJSMethod\":\"goToPage\",\"active_type\":\"2\"}")
				.header("Content-Type", "application/json")
				.header("Cookie", "JSESSIONID=D0FEEA67494F2ADC6CDB491248C845D5; _gscu_1677760547=30758873htcr1i12; Hm_lvt_9ac0f18d7ef56c69aaf41ca783fcb10c=1530760134,1530760280,1531101515,1531113348; session_id=46547e1e-4bf2-456b-acad-d0acd602540d")
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
				.ignoreContentType(true)
				.method(Method.POST)
				.execute().body();
		System.out.println(res);
		JSONObject obj = JSON.parseObject(res);
		Document document = Jsoup.parse(obj.getString("data"));
		Elements e = document.select("th:contains(开始时间)").next();
	}
	@Test
	public void test3() throws Exception{
		String json = "{\"currPage\":1,\"pageJSMethod\":\"goToPage\",\"active_type\":\"2\"}";
		URL url = new URL("http://zzfws.bjjs.gov.cn/enroll/dyn/enroll/viewEnrollHomePager.json");
		String result = "";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        // 设置文件类型:
        conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
        conn.setRequestProperty("Cookie", "JSESSIONID=D0FEEA67494F2ADC6CDB491248C845D5; _gscu_1677760547=30758873htcr1i12; Hm_lvt_9ac0f18d7ef56c69aaf41ca783fcb10c=1530760134,1530760280,1531101515,1531113348; session_id=46547e1e-4bf2-456b-acad-d0acd602540d");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
        OutputStream outwritestream = conn.getOutputStream();
        outwritestream.write(json.getBytes());
        outwritestream.flush();
        outwritestream.close();
        int httpcode = conn.getResponseCode();
        if (conn.getResponseCode() == 200) {
        	Scanner scanner = new Scanner(conn.getInputStream(), "utf-8");
			while (scanner.hasNextLine()) {
				result += scanner.nextLine();
			}
			scanner.close();
        }
        conn.disconnect();
		System.out.println(result);
	}
	@Test
	public void test2() throws Exception{
		Map map = DBUtil.getObject("select * from daily where code=? and cdate=(select max(cdate) from daily where code=?)", new Object[]{"603919","603919"});
		Double c = parseDouble(map.get("close")+"a");
		Double a = parseDouble(map.get("amount")+"");
		Double aa = parseDouble(null);
		Double cc = parseDouble("-21.36");
		boolean ba = a.compareTo(aa)!=0;
		boolean bc = c.compareTo(cc)!=0;
		
	}
	@Test
	public void test1() throws Exception{
		Stock stock = new com.app.Stock();
		//季报数据   业绩报表
		Calendar c = Calendar.getInstance();
		c.set(2015, Calendar.DECEMBER,31);
		
		Calendar e = Calendar.getInstance();
		e.set(2017, Calendar.MARCH,31);
		
		while(true){
			if(c.getTimeInMillis()<=e.getTimeInMillis()){
				String jidu = String.format("%tF", c.getTime());
				c.set(Calendar.DAY_OF_MONTH, 30);
				String savejidu = String.format("%tF", c.getTime());
				
				stock.getQuarterReport(jidu, savejidu);
				stock.getQuarterZhuli(jidu, savejidu);
				
				c.add(Calendar.MONTH, 3);
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
			}else{
				break;
			}
		}
	}
	private Double parseDouble(String s){
		try{
			Double d = Double.valueOf(s);
			return d;
		}catch(Exception e){
			return null;
		}
	}
}