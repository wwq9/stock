package com.app;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@WebListener
public class SimpleMailSender implements ServletContextListener {
	static String to1 = "84529527@qq.com";
	static String to2 = "cj.h@qq.com";
	static Transport tr = null;
	static Timer timer = new Timer();
	static Pattern cn = Pattern.compile("\\d{4}-\\d{2}-\\d{2} (\\d{6}) ");// 匹配年月日后面的code
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		timer.cancel();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
//		runOwn();
//		runKehu();
		runYjyg();
		runGycq();
	}
	
	private void runKehu() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 14);
		c.set(Calendar.MINUTE, 0);
		
		TimerTask quarter = new TimerTask() {
			public void run() {
				if(10 == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)){
					try{Thread.sleep(RandomUtils.nextInt(5*60*1000));}catch(Exception e){}
					SimpleMailSender.kehu();
				}
			}
		};
		timer.scheduleAtFixedRate(quarter, c.getTime(), 24*60*60*1000);
	}
	private void runOwn() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 13);
		c.set(Calendar.MINUTE, 30);
		
		TimerTask quarter = new TimerTask() {
			public void run() {
				try{Thread.sleep(RandomUtils.nextInt(5*60*1000));}catch(Exception e){}
				SimpleMailSender.Own();
			}
		};
		timer.scheduleAtFixedRate(quarter, c.getTime(), 24 * 60 * 60 * 1000);
	}
	private void runYjyg() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 8);
		c.set(Calendar.MINUTE, 30);
		
		TimerTask quarter = new TimerTask() {
			public void run() {
				try{Thread.sleep(RandomUtils.nextInt(5*60*1000));}catch(Exception e){}
				SimpleMailSender.runQuarterYjyg();
			}
		};
		timer.scheduleAtFixedRate(quarter, c.getTime(), 24 * 60 * 60 * 1000);
	}

	private void runGycq() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 10);
		c.set(Calendar.MINUTE, 0);
		TimerTask quarter = new TimerTask() {
			public void run() {
				try{Thread.sleep(RandomUtils.nextInt(5*60*1000));}catch(Exception e){}
				SimpleMailSender.gycq();
			}
		};
		timer.scheduleAtFixedRate(quarter, c.getTime(), 24 * 60 * 60 * 1000);
	}

	public static void sendHtmlMail(String subject, String tx,String to) {
		Properties p = new Properties();
		p.setProperty("mail.smtp.host", "smtp.qq.com");
		p.setProperty("mail.smtp.port", "587");
		p.setProperty("mail.smtp.auth", "true");
		p.setProperty("mail.transport.protocol", "smtp");

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("84529527@qq.com","xkqtrjfqyrwmbidf");
			}
		};
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session mailSession = Session.getDefaultInstance(p, auth);
		
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(mailSession);
			// 创建邮件发送者地址
			mailMessage.setFrom(new InternetAddress("84529527@qq.com"));
			// 创建邮件的接收者地址，并设置到邮件消息中
			mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			// 设置邮件消息的主题
			mailMessage.setSubject(MimeUtility
					.encodeText(subject, "UTF-8", "B"));
			// 设置HTML内容
			mailMessage.setContent(tx, "text/html; charset=utf-8");
			// 发送邮件
			synchronized (SimpleMailSender.class) {
				if (tr == null) {
					tr = mailSession.getTransport();
				}
				if (!tr.isConnected()) {
					tr.connect();
				}
			}
			tr.sendMessage(mailMessage, mailMessage.getAllRecipients());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static  void runQuarterYjyg() {
		String url = "http://datainterface.eastmoney.com/EM_DataCenter/JS.aspx?type=SR&sty=YJYG&fd=%s&st=4&sr=-1&p=%s&ps=50&js={pages:(pc),data:[(x)]}&stat=0&rt=%s";
		Calendar c = Calendar.getInstance();
		Date cd = c.getTime();
		String q = "";
		if (c.get(Calendar.MONTH) < 3) {
			c.set(Calendar.MONTH, 2);
			c.set(Calendar.DAY_OF_MONTH, 31);
		} else if (c.get(Calendar.MONTH) < 6) {
			c.set(Calendar.MONTH, 5);
			c.set(Calendar.DAY_OF_MONTH, 30);
		} else if (c.get(Calendar.MONTH) < 9) {
			c.set(Calendar.MONTH, 8);
			c.set(Calendar.DAY_OF_MONTH, 30);
		} else {
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DAY_OF_MONTH, 31);
		}
		q = String.format("%tF", c.getTime());
		try {
			File f = new File("/data/sk/sk.properties");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			Properties p = new Properties();
			FileReader reader = new FileReader(f);
			p.load(reader);
			reader.close();
			String start = p.getProperty("lastDate", String.format("%tF", cd));
			Object[] obj = getQuarterYjyg(String.format(url, q, "1",
					System.currentTimeMillis() / 30000), start);
			if (obj != null) {
				int pages = (Integer) obj[0];
				boolean b = (Boolean) obj[1];
				String mxdate = String.format("%tF", obj[2]);
				String re = (String) obj[3];
				if (b) {
					for (int i = 2; i <= pages; i++) {
						try {
							Thread.sleep(new Random().nextInt(1000));
						} catch (Exception e) {
						}
						;
						Object[] obj2 = getQuarterYjyg(
								String.format(url, q, i,
										System.currentTimeMillis() / 30000),
								start);
						if (obj2 != null) {
							boolean b2 = (Boolean) obj2[1];
							if (!b2) {
								break;
							} else {
								re += (String) obj2[3];
							}
						}
					}
				}
				if (re != null && !"".equals(re)) {
					sendHtmlMail("Stock Report Notice", re,to1);
					Matcher matcher = cn.matcher(re);
					while (matcher.find()) {
						mxdate += "," + matcher.group(1);
					}
					p.setProperty("lastDate", mxdate);
					FileWriter writer = new FileWriter(f);
					p.store(writer, "");
					writer.close();
				}
			}
		} catch (Exception e) {
		}
	}

	public static  Object[] getQuarterYjyg(String url, String lastdatecode) {
		Object[] obj = null;
		Scanner scanner = null;
		try {
			String[] datecode = lastdatecode.split(",");
			Date lastdate = sdf.parse(datecode[0]);
			URL q_url = new URL(String.format(url));
			scanner = new Scanner(q_url.openStream(), "utf-8");
			String str = "";
			while (scanner.hasNextLine()) {
				str += scanner.nextLine();
			}
			JSONObject jo = JSON.parseObject(str);
			int pages = jo.getIntValue("pages");
			boolean b = true;
			String re = "";
			Date temp = lastdate;
			JSONArray ja = jo.getJSONArray("data");
			for (int i = 0; i < ja.size(); i++) {
				Object object = ja.get(i);
				if (object instanceof String) {
					String[] one = ((String) object).split(",");
					Date ygdate = sdf.parse(one[7]);
					if (ygdate.getTime() >= lastdate.getTime()) {
						String code = one[0];
						String name = one[1];
						String tx = one[2];
						boolean bb = true;
						for (int j = 1; j < datecode.length; j++) {
							if (code.equals(datecode[j])
									&& ygdate.getTime() == lastdate.getTime()) {
								bb = false;
								break;
							}
						}
						if (bb) {
							re += String.format("%tF", ygdate) + " " + code
									+ " " + name + " " + tx + "<br/>";
						}
						if (i == 0) {
							temp = ygdate;
						}
					} else {
						b = false;
						break;
					}
				}
			}
			lastdate = temp;
			obj = new Object[4];
			obj[0] = pages; // 页数
			obj[1] = b;// 是否循环第2页以后的
			obj[2] = lastdate;// 预告的最新日期
			obj[3] = re;// 预告内容拼接
		} catch (Exception e) {
			sendHtmlMail("东方财富业绩预告连接超时", "",to1);
		} finally {
			if (scanner != null)
				scanner.close();
		}
		return obj;
	}

	/*
	 * 共有产权
	 */
	public static  void gycq() {
		HttpURLConnection conn = null;
		Scanner scanner = null;
		try {
			String json = "{\"currPage\":1,\"pageJSMethod\":\"goToPage\",\"active_type\":\"2\"}";
			URL url = new URL(
					"http://zzfws.bjjs.gov.cn/enroll/dyn/enroll/viewEnrollHomePager.json");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置文件类型:
			conn.setRequestProperty("Content-Type",
					"application/json;charset=UTF-8");
			conn.setRequestProperty(
					"Cookie",
					"JSESSIONID=D0FEEA67494F2ADC6CDB491248C845D5; _gscu_1677760547=30758873htcr1i12; Hm_lvt_9ac0f18d7ef56c69aaf41ca783fcb10c=1530760134,1530760280,1531101515,1531113348; session_id=46547e1e-4bf2-456b-acad-d0acd602540d");
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
			OutputStream outwritestream = conn.getOutputStream();
			outwritestream.write(json.getBytes());
			outwritestream.flush();
			outwritestream.close();
			if (conn.getResponseCode() == 200) {
				scanner = new Scanner(conn.getInputStream(), "utf-8");
				String result = "";
				while (scanner.hasNextLine()) {
					result += scanner.nextLine();
				}
				if (result != null && !"".equals(result)
				// &&(result.contains("海淀")||result.contains("丰台"))
				) {
					JSONObject obj = JSON.parseObject(result);
					String re = obj.getString("data");
					if (StringUtils.isNotBlank(re)) {
						Element dateElement = Jsoup.parse(re)
								.select("th:contains(开始时间)").next().first();
						if(dateElement!=null){
							String date = dateElement.ownText();
							File f = new File("/data/sk/shengou.properties");
							if (!f.exists()) {
								f.getParentFile().mkdirs();
								f.createNewFile();
							}
							Properties p = new Properties();
							FileReader reader = new FileReader(f);
							p.load(reader);
							reader.close();
							String lastdate = p.getProperty("lastDate");
							if (!date.equals(lastdate)) {
								p.setProperty("lastDate", date);
								FileWriter writer = new FileWriter(f);
								p.store(writer, "");
								writer.close();
								sendHtmlMail("共有产权申购", re,to1);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			sendHtmlMail("共有产权申购连接超时", "",to1);
		} finally {
			if (scanner != null)
				scanner.close();
			if (conn != null)
				conn.disconnect();
		}
		
		File f=null;
		Properties p= null;
		try {
			f = new File("/data/sk/gonggao.properties");
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			p = new Properties();
			FileReader reader = new FileReader(f);
			p.load(reader);
			reader.close();
		} catch (Exception e) {}
		String lastdate = p.getProperty("lastDate");
		String ftlastdate = p.getProperty("ftlastDate");
		
		try {
			Document doc = Jsoup
					.connect("http://www.bjhd.gov.cn/xxgk/auto4522_51806/index_bm.shtml")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
					.get();
			String date = doc.select("td span").get(1).ownText();
			Elements hdas = doc.select("td[class=mc]");
			StringBuilder sb = new StringBuilder("<ul>");
			for (Element a : hdas) {
				String as = a.html();
				int s= as.indexOf("<a");
				int e = as.indexOf("</a>")+4;
				as = as.substring(s, e).replaceAll("\\./", "http://www.bjhd.gov.cn/xxgk/auto4522_51806/");
				sb.append("<li>"+as+"</li>");
			}
			sb.append("</ul>");
			if (!date.equals(lastdate)) {
				p.setProperty("lastDate", date);
				sendHtmlMail("海淀共有产权公告", sb.toString(),to1);
			}
		} catch (Exception e) {
			sendHtmlMail("海淀共有产权公告连接超时", "",to1);
		}
		try{
			Document fengtaiDoc = Jsoup.connect("http://www.bjft.gov.cn/XXGK/BZXZFGS/list.xml")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
					.get();
			String ftdate = fengtaiDoc.selectFirst("pubtime").ownText();
			Elements titles = fengtaiDoc.select("title");
			Elements ids = fengtaiDoc.select("id");
			StringBuilder sb = new StringBuilder("<ul>");
			for (int i=0;i<titles.size();i++) {
				String title = titles.get(i).ownText();
				String id = ids.get(i).ownText();
				sb.append("<li><a href='http://www.bjft.gov.cn/n_shownews.html?"+id+"?/XXGK/BZXZFGS/'>"+title+"</a></li>");
			}
			sb.append("</ul>");
			if (!ftdate.equals(ftlastdate)) {
				p.setProperty("ftlastDate", ftdate);
				sendHtmlMail("丰台共有产权公告", sb.toString(),to1);
			}
		} catch (Exception e) {
			sendHtmlMail("丰台共有产权公告连接超时", "",to1);
		}
			
		try {
			FileWriter writer = new FileWriter(f);
			p.store(writer, "");
			writer.close();
		} catch (Exception e) {}
		
	}
	public static  void Own(){
		List<String[]> userList = new ArrayList();
		userList.add(new String[]{"18600369418","Mdffxwwq9988"});
		userList.add(new String[]{"18513650104","w3650104"});
		userList.add(new String[]{"18688690526","hcj10120"});
		userList.add(new String[]{"13522643008","hcj10120"});
		
		StringBuffer mailContent = new StringBuffer();
		for(String[] user:userList){
			Document myAccountDoc = buyTF(user[0], user[1]);
			if(myAccountDoc != null){
				Element commission = myAccountDoc.selectFirst("div:containsOwn(我的佣金)").parent();
				Element asset = myAccountDoc.selectFirst("div:containsOwn(我的资产)").parent();
				mailContent.append("<div style='border:1px solid;'>"
						+ "<h3>"+myAccountDoc.selectFirst("span[fullname]").text()+user[0]+"</h3>");
				mailContent.append(commission);
				mailContent.append(asset+"</div><br/>");
			}
		}
		if(mailContent.length()!=0) sendHtmlMail("我的账户",mailContent.toString(),to2);
	}
	public static  void kehu(){
		List<String[]> userList = new ArrayList();
		userList.add(new String[]{"15810231627","j0231627"});
		userList.add(new String[]{"13552594269","w2594269"});
		userList.add(new String[]{"13521170324","t1170324"});
		userList.add(new String[]{"13439345891","c9345891"});
		userList.add(new String[]{"13520697342","j0697342"});
		userList.add(new String[]{"13718212179","fgh18212179"});
		userList.add(new String[]{"13718160716","w8160716"});
		userList.add(new String[]{"13051419637","y1419637"});
		userList.add(new String[]{"15601260653","w1260653"});
		userList.add(new String[]{"13521326638","h1326638"});
		userList.add(new String[]{"13121589851","z1589851"});
		userList.add(new String[]{"13611281635","h1281635"});
		userList.add(new String[]{"13552898104","h2898104"});
		userList.add(new String[]{"18910898086","l0898086"});
		userList.add(new String[]{"13366106193","m6106193"});
		userList.add(new String[]{"13581986656","m1986656"});
		userList.add(new String[]{"15601077336","x1077336"});
		userList.add(new String[]{"13611066268","s1066268"});
		
		StringBuffer mailContent = new StringBuffer();
		for(String[] user:userList){
			Document myAccountDoc = buyTF(user[0], user[1]);
			if(myAccountDoc != null){
				Element commission = myAccountDoc.selectFirst("div:containsOwn(我的佣金)").parent();
				Element asset = myAccountDoc.selectFirst("div:containsOwn(我的资产)").parent();
				mailContent.append("<div style='border:1px solid;'>"
						+ "<h3>"+myAccountDoc.selectFirst("span[fullname]").text()+user[0]+"</h3>");
				mailContent.append(commission);
				mailContent.append(asset+"</div><br/>");
			}
		}
		if(mailContent.length()!=0) sendHtmlMail("我的账户",mailContent.toString(),to2);
	}
	public static  Document buyTF(String p1, String p2) {
		Document myAccountDoc = null;
		// 打开cookie管理
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
		try {
			// 登录页
			Document loginDoc = Jsoup
					.connect("https://www.thankfund.com/login")
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
					.get();
		} catch (Exception e) {
		}
		try {
			// 登录动作
			String loginRes = Jsoup
					.connect("https://www.thankfund.com/login_mobile")
					.userAgent(
							"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
					.referrer("https://www.thankfund.com/login")
					.data("mobile", p1, "password", p2,
							"captcha", "").ignoreContentType(true)
					.method(Method.POST).execute().body();
			JSONObject loginResJson = JSON.parseObject(loginRes);
			if (200 == loginResJson.getIntValue("status")) {
				JSONObject datas = JSON.parseObject(loginResJson
						.getString("datas"));
				JSONObject user = datas.getJSONObject("user");
				String fund_token = datas.getString("token");
				JSONObject fundUser = new JSONObject();
				fundUser.put("role_type", datas.getString("role_type"));
				fundUser.put("user_id", datas.getString("user_id"));
				fundUser.put("mobile", user.getString("mobile"));
				fundUser.put("auth_state", user.getString("auth_state"));
				fundUser.put("has_pay_password",
						user.getIntValue("trade_password_state"));
				fundUser.put("has_bank_card_no",
						user.getIntValue("bankcard_bound_state"));
				fundUser.put("invite_code", user.getString("customer_no"));

				Map extCookie = new HashMap();
				extCookie.put("fund_token", fund_token);
				extCookie.put("fund_captcha_show", "false");
				extCookie.put("fund_user",
						URLEncoder.encode(fundUser.toJSONString(), "UTF-8"));
				// 我的账户
				myAccountDoc = Jsoup
						.connect("https://www.thankfund.com/my_account")
						.userAgent(
								"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
						.referrer("https://www.thankfund.com/login")
						.cookies(extCookie).get();
				
				myAccountDoc.body().append("<span fullname>"+user.getString("full_name")+"</span>");
				
				int balance = Double.valueOf(
						myAccountDoc.selectFirst("span:containsOwn(账户余额)")
								.selectFirst("strong").html()
								.replaceAll("[, ]", "")).intValue();
				if (balance >= 100) {
					// 提交余额购买
					String buyRes = Jsoup
							.connect(
									"https://www.thankfund.com/invest/balanceBuyProducts")
							.userAgent(
									"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
							.referrer(
									"https://www.thankfund.com/invest/detail/683068a8602c474a8d1746549a211b51")
							.cookies(extCookie)
							.data("amount", balance + "00")
							.data("customer_id", fundUser.getString("user_id"))
							.data("pay_amount", balance + "00")
							.data("product_id",
									"683068a8602c474a8d1746549a211b51")
							.ignoreContentType(true).method(Method.POST)
							.execute().body();
					if (200 != JSON.parseObject(buyRes).getIntValue("status")) {
						sendHtmlMail("提交余额购买失败"+p1, buyRes,to2);
					}
				}

				String logoutRes = Jsoup
						.connect("https://www.thankfund.com/logout")
						.userAgent(
								"Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko")
						.referrer("https://www.thankfund.com/my_account")
						.cookies(extCookie).ignoreContentType(true)
						.method(Method.POST).execute().body();
				if (200 != JSON.parseObject(logoutRes).getIntValue("status")) {
					sendHtmlMail("退出登录失败"+p1, logoutRes,to1);
				}
			} else {
				sendHtmlMail("登录失败"+p1, loginRes,to2);
			}
		} catch (Exception e) {
			sendHtmlMail("ThankFund连接超时"+p1, "",to1);
		}
		return myAccountDoc;
	}
}