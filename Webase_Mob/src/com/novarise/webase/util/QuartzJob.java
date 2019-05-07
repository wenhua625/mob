package com.novarise.webase.util;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.novarise.webase.framework.AppSmsAynSender;

public class QuartzJob implements Job {

	// 该方法实现需要执行的任务
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		
		 
		  JobDetail jobDetail = arg0.getJobDetail(); 
		  String jobname=jobDetail.getKey().getName();
		  AppSmsAynSender a= new AppSmsAynSender();
		  System.out.println(jobname +"...");
		  if(jobname.equalsIgnoreCase("job1")) 
		  { 
			  //String sql="select '订单跟进：今天有'+convert(varchar,COUNT(*))+'个客户需要跟进，别忘了跟进哦~~' nr, device_token token1,lxfs msgTel,'' sendTel,'艾管理' appname,'' ordercode,'订单跟进' msgtype from OrderList a,TJ_SYS_YH b where dbo.getDateStr1(YJLXSJ)=dbo.getDateStr1(GETDATE()) and a.YWY_TEL=b.lxfs group by LXFS,device_token";
		      //丢入公海前提�?
			  String gh_sql="select '跟进提醒：业主　'+arr_man+'('+arr_tel+') '+arr_address+arr_address_detail +";
			  gh_sql+="' ，已经到了跟进时间，今天别忘了跟进哦~~~' nr, '' token1,ywy_tel msgTel,'' sendTel,'艾管理' appname,order_code ordercode,'订单跟进' msgtype  ";
			  gh_sql+="from OrderList a,agent_list b  " ;
			  gh_sql+=" where datediff(dd,yjlxsj,getdate())=0 ";
			 gh_sql+="and a.agent_code=b.agent_code and ywy_tel!=''  and a.AGENT_CODE='31013'";
			 // System.out.println("gh_sql:"+gh_sql);
			  
			  /*String gh_sql="select '跟进提醒：业主　'+arr_man+'('+arr_tel+') '+arr_address+arr_address_detail +' 超过'+convert(varchar,isnull(max_hsts,5))+'天没跟进，再不跟进将丢到公海�? nr, '' token1,ywy_tel msgTel,'' sendTel,'艾管理' appname,order_code ordercode,'订单跟进' msgtype  ";
		    		 gh_sql+="  from OrderList a,agent_list b  ";
		    	     gh_sql+="where (isnull(max_hsts,5)-datediff(dd,zjlxsj,getdate()))=1  ";
		    		 gh_sql+="and a.agent_code=b.agent_code and ywy_tel!='' and order_sts='9'";*/
			//  a.sendMsgForSql(gh_sql);
		  }
		 
		// 提醒店长或老板，店内所有的签单数。
		
		 if(jobname.equalsIgnoreCase("job2"))
			{  
				  //店铺简报 
			    //  String sql1="select agent_name+' 经营简报今日新增客户'+convert(varchar,xzkhs)+'个，进店'+convert(varchar,jdkhs)+'个，成交'+convert(varchar,cjkhs)+'单，回款'+convert(varchar,jrhke)+'元，签订合同 '+convert(varchar,qdhte)+'元，累计'+convert(varchar,lxwke)+'元尾款未收到' nr, agent_tel token1,agent_tel msgTel,'' sendTel,'艾管理' appname,'' ordercode,'经营简报' msgtype  from v_rjyjb a,agent_list b where a.agent_code=b.agent_code ";
			     String sql1 = "select agent_name+' 经营简报：本月收款'+CONVERT(varchar,ISNULL(this_amount,0))+ '元，较上月'+ CONVERT(varchar,CONVERT(decimal(18,2),ISNULL(zzl,0)*100))+'%' nr,'' token1,agent_tel msgTel,ISNULL(zzl,0) sendTel,'艾管理' appname,ISNULL(this_amount,0) ordercode,'经营简报' msgtype from v_jyfxMonth v right join (select * from agent_list where  DATEDIFF(DD,GETDATE(),end_date) > -30) a on a.AGENT_CODE=v.agent_code where LEN(agent_tel) =11"
			    		 +" and a.agent_code='31013'";
			    		 //			     		 + "  union  " 
//			    		 +"select nr,token1,ISNULL(lxfs,msgtel) msgtel,sendTel,appname,ordercode,msgtype from  ( select a.agent_code,agent_name+' 经营简报：本月收款'+CONVERT(varchar,ISNULL(this_amount,0))+ '元，较上月'+ CONVERT(varchar,CONVERT(decimal(18,2),ISNULL(zzl,0)*100))+'%' nr,'' token1,agent_tel msgTel,ISNULL(zzl,0) sendTel,'艾管理' appname,ISNULL(this_amount,0) ordercode,'经营简报' msgtype from v_jyfxMonth v right join "
//			     		 +"(select * from agent_list where  DATEDIFF(DD,GETDATE(),end_date) > -30) a on a.AGENT_CODE=v.agent_code where LEN(agent_tel) !=11 ) b  left join tj_sys_yh t on t.dept=b.AGENT_CODE and   YHJB='老板'";
			     System.out.println("job2:"+sql1);
			     	     a.sendMsgForSql(sql1);
			}
		 //考勤提醒
		 if(jobname.equalsIgnoreCase("job3"))
		 {
			 String appkey1="4f14eb68ed1387a4f87f0d19";
		     String masterSecret1="d74f9c4c69d6833d10311b0d";
			// a.sendMsgForContent("考勤：要记得上班打卡签到哦~~~", "考勤提醒", appkey1, masterSecret1);
		 }
		//考勤提醒
		 if(jobname.equalsIgnoreCase("job4"))
		 {
			 String appkey1="4f14eb68ed1387a4f87f0d19";
		     String masterSecret1="d74f9c4c69d6833d10311b0d";
			 //a.sendMsgForContent("考勤：要记得下班打卡签退哦~~~", "考勤提醒", appkey1, masterSecret1);
		 }
			 

		/*
		 * //提醒导购，今日是否签单。if(jobname.equalsIgnoreCase("ordersaletipjob")) {
		 * String sql1=
		 * "select  case sign(COUNT(*)) when 0 then '今日您还没签单，要加油哦!' else '你今日已成交'+convert(varchar,COUNT(*))+'单，点击到数据统计里查看！ end nr, kcc_token token1,lxfs msgTel,'' sendTel,'客串�? appname from OrderList a,TJ_SYS_YH b where dbo.getDateStr1(order_date) = dbo.getDateStr1(GETDATE()) and a.YWY_TEL=b.lxfs  and YHJB in ('导购') group by LXFS,kcc_token"
		 * ; a.sendMsgForSql(sql1); } //提醒店长或老板，今日是否签单。
		 * if(jobname.equalsIgnoreCase("orderheadsaletipjob")) { String sql1=
		 * "select  case sign(COUNT(*)) when 0 then '今日您还没签单，要加油哦!' else '你今日已成交'+convert(varchar,COUNT(*))+'单，点击到数据统计里查看！ end nr, device_token token1,lxfs msgTel,'' sendTel,'艾管理' appname from OrderList a,TJ_SYS_YH b where dbo.getDateStr1(order_date) = dbo.getDateStr1(GETDATE()) and a.YWY_TEL=b.lxfs  and YHJB in ('店长','老板') group by LXFS,device_token"
		 * ; a.sendMsgForSql(sql1); }
		 */

		// 提醒店长或老板，店内所有的签单数�?
		/*
		 * if(jobname.equalsIgnoreCase("orderheadsaletipjob_all")) { String
		 * sql1=
		 * "select case sign(ordernum) when 0 then '经营简报：今天没有签单，又白忙活了~~' else '经营简报：今天已签单+convert(varchar,ordernum)+'单，明天继续加油~~�? end nr, device_token token1,lxfs msgTel,'' sendTel,'艾管理' appname  from TJ_SYS_YH a,(select agent_code,COUNT(*) ordernum from OrderList where dbo.getDateStr1(order_date) = dbo.getDateStr1(GETDATE()) group by agent_code) b where a.DEPT=b.AGENT_CODE and  YHJB in ('店长','老板')"
		 * ; a.sendMsgForSql(sql1); }
		 */

	}

	public void go() throws Exception {
		// 首先，必需要取得一个Scheduler的引用
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		// jobs可以在scheduled的sched.start()方法前被调用

		// job 每天10,14,16点执行
		JobDetail job = newJob(QuartzJob.class).withIdentity("job1", "group1")
				.build();
//		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1")
//				.withSchedule(cronSchedule("0 0 10,14,16 * * ?")).build();  
		CronTrigger trigger = newTrigger().withIdentity("trigger1", "group1")
				.withSchedule(cronSchedule("5/15 * * * * ?")).build();  
		Date ft = sched.scheduleJob(job, trigger);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		
		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		//job 2将每天22执行
		job = newJob(QuartzJob.class).withIdentity("job2", "group1").build();
		trigger = newTrigger().withIdentity("trigger2", "group1")
				.withSchedule(cronSchedule("5/15 * * * * ?")).build();  
				//.withSchedule(cronSchedule("0 0 22 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
	   //考勤提醒
		job = newJob(QuartzJob.class).withIdentity("job3", "group1").build();
		trigger = newTrigger().withIdentity("trigger3", "group1")
				.withSchedule(cronSchedule("0 15 8,9 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
		
		//考勤提醒
				job = newJob(QuartzJob.class).withIdentity("job4", "group1").build();
				trigger = newTrigger().withIdentity("trigger4", "group1")
						.withSchedule(cronSchedule("0 21 16,17,18 * * ?")).build();
				ft = sched.scheduleJob(job, trigger);
				System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
						+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// 开始执行，start()方法被调用后，计时器就开始工作，计时调度中允许放入N个Job
		sched.start();
		
	}

	
	public static void main(String[] args) throws Exception { 
		 
		QuartzJob test = new QuartzJob(); 
        test.go(); 
    } 
}
