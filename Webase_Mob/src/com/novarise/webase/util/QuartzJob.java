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
import com.novarise.webase.framework.AppSmsAynSenderJob;

public class QuartzJob implements Job {

	// 该方法实现需要执行的任务
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		
		 
		  JobDetail jobDetail = arg0.getJobDetail(); 
		  String jobname=jobDetail.getKey().getName();
		  AppSmsAynSenderJob a= new AppSmsAynSenderJob();
		  System.out.println(jobname +"...");
		  if(jobname.equalsIgnoreCase("job1")) 
		  { 
			  String gh_sql="select '业主　'+arr_man+'('+arr_tel+') '+arr_address+arr_address_detail +' ，已经到了跟进时间，今天别忘了跟进哦~~~' nr,  a.agent_code,'客户跟进提醒' msgtype,ywy_tel msgTel,'艾管理' appname,order_code ,arr_man,arr_tel,arr_address,'' col_amount,'' col_memo "
					  	+" from OrderList a,agent_list b  where datediff(dd,yjlxsj,getdate())=0 and a.agent_code=b.agent_code and ywy_tel!=''  and a.AGENT_CODE='31013'";
			  System.out.println("gh_sql:"+gh_sql);
			  
			  /*String gh_sql="select '跟进提醒：业主　'+arr_man+'('+arr_tel+') '+arr_address+arr_address_detail +' 超过'+convert(varchar,isnull(max_hsts,5))+'天没跟进，再不跟进将丢到公海�? nr, '' token1,ywy_tel msgTel,'' sendTel,'艾管理' appname,order_code ordercode,'订单跟进' msgtype  ";
		    		 gh_sql+="  from OrderList a,agent_list b  ";
		    	     gh_sql+="where (isnull(max_hsts,5)-datediff(dd,zjlxsj,getdate()))=1  ";
		    		 gh_sql+="and a.agent_code=b.agent_code and ywy_tel!='' and order_sts='9'";*/
			  a.sendMsgForSql(gh_sql);
		  }
		 
		// 提醒店长或老板，店内所有的签单数。
		
		 if(jobname.equalsIgnoreCase("job2"))
			{  
				  //店铺简报 
			    //  String sql1="select agent_name+' 经营简报今日新增客户'+convert(varchar,xzkhs)+'个，进店'+convert(varchar,jdkhs)+'个，成交'+convert(varchar,cjkhs)+'单，回款'+convert(varchar,jrhke)+'元，签订合同 '+convert(varchar,qdhte)+'元，累计'+convert(varchar,lxwke)+'元尾款未收到' nr, agent_tel token1,agent_tel msgTel,'' sendTel,'艾管理' appname,'' ordercode,'经营简报' msgtype  from v_rjyjb a,agent_list b where a.agent_code=b.agent_code ";
//			     String sql1 = "select agent_name+' 经营简报：本月收款'+CONVERT(varchar,ISNULL(this_amount,0))+ '元，较上月'+ CONVERT(varchar,CONVERT(decimal(18,2),ISNULL(zzl,0)*100))+'%' nr,'' token1,agent_tel msgTel,ISNULL(zzl,0) sendTel,'艾管理' appname,ISNULL(this_amount,0) ordercode,'经营简报' msgtype from v_jyfxMonth v right join (select * from agent_list where  DATEDIFF(DD,GETDATE(),end_date) > -30) a on a.AGENT_CODE=v.agent_code where LEN(agent_tel) =11"
//			    		 +" and a.agent_code='31013'";
			    		 //			     		 + "  union  " 
//			    		 +"select nr,token1,ISNULL(lxfs,msgtel) msgtel,sendTel,appname,ordercode,msgtype from  ( select a.agent_code,agent_name+' 经营简报：本月收款'+CONVERT(varchar,ISNULL(this_amount,0))+ '元，较上月'+ CONVERT(varchar,CONVERT(decimal(18,2),ISNULL(zzl,0)*100))+'%' nr,'' token1,agent_tel msgTel,ISNULL(zzl,0) sendTel,'艾管理' appname,ISNULL(this_amount,0) ordercode,'经营简报' msgtype from v_jyfxMonth v right join "
//			     		 +"(select * from agent_list where  DATEDIFF(DD,GETDATE(),end_date) > -30) a on a.AGENT_CODE=v.agent_code where LEN(agent_tel) !=11 ) b  left join tj_sys_yh t on t.dept=b.AGENT_CODE and   YHJB='老板'";
			 String sql1= "select  nr,   AGENT_CODE ,msgtype,LXFS,appname,'' order_code,'' arr_man,'' arr_telm,'' arr_address,this_amount,zzl from ( select a.AGENT_CODE,AGENT_NAME+' 本月收款'+CONVERT(varchar,ISNULL(this_amount,0))+ '元，较上月'+ CONVERT(varchar,CONVERT(decimal(18,2),ISNULL(zzl,0)*100))+'%' nr,"+
							" ISNULL(zzl,0) zzl,'艾管理' appname,ISNULL(this_amount,0) this_amount,'经营简报' msgtype from v_jyfxMonth v right join (select * from agent_list where  DATEDIFF(DD,GETDATE(),end_date) > -30) a on a.AGENT_CODE=v.agent_code "+
							") x left join tj_sys_yh t on QX_MOB_DP like('%'+ x.AGENT_CODE +'%') where YHJB in('老板','店长') " ;
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
				.withSchedule(cronSchedule("0/15 * * * * ?")).build();  
		Date ft = sched.scheduleJob(job, trigger);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		
		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		//job 2将每月1号9：30执行
		job = newJob(QuartzJob.class).withIdentity("job2", "group1").build();
		trigger = newTrigger().withIdentity("trigger2", "group1")
				.withSchedule(cronSchedule("0 30 09 1 * ?")).build();  
				//.withSchedule(cronSchedule("0 0 22 * * ?")).build();
		ft = sched.scheduleJob(job, trigger);
		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
	   //考勤提醒
//		job = newJob(QuartzJob.class).withIdentity("job3", "group1").build();
//		trigger = newTrigger().withIdentity("trigger3", "group1")
//				.withSchedule(cronSchedule("0 15 8,9 * * ?")).build();
//		ft = sched.scheduleJob(job, trigger);
//		System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
//				+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());
//		
		//考勤提醒
//				job = newJob(QuartzJob.class).withIdentity("job4", "group1").build();
//				trigger = newTrigger().withIdentity("trigger4", "group1")
//						.withSchedule(cronSchedule("0 21 16,17,18 * * ?")).build();
//				ft = sched.scheduleJob(job, trigger);
//				System.out.println(job.getKey() + " 已被安排执行于 " + sdf.format(ft)
//						+ "，并且以如下重复规则重复执行: " + trigger.getCronExpression());

		// 开始执行，start()方法被调用后，计时器就开始工作，计时调度中允许放入N个Job
		sched.start();
		
	}

	
	public static void main(String[] args) throws Exception { 
		 
		QuartzJob test = new QuartzJob(); 
        test.go(); 
    } 
}
