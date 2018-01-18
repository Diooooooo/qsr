package com.qsr.sdk.config;

import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IContainerFactory;
import com.jfinal.plugin.druid.DruidPlugin;
import com.qsr.sdk.controller.*;
import com.qsr.sdk.jfinal.plugin.event.EventPlugin;
import com.qsr.sdk.startup.Startup;
import com.qsr.sdk.util.WorkingResourceUtil;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Config extends JFinalConfig {

	@Override
	public void configConstant(Constants me) {

		// RenderFactory.setErrorRenderFactory(new IErrorRenderFactory() {
		//
		// @Override
		// public Render getRender(int errorCode, String view) {
		// JsonRender render = new JsonRender(new Result(errorCode,
		// "service internal error"));
		// return render;
		// }
		// });
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/", HomeController.class);
		me.add("/user", UserController.class);
		me.add("/push", PushController.class);
		me.add("/sms", SmsController.class);
		me.add("/oauth2", OAuth2Controller.class);
		me.add("/entry", EntryController.class);
		me.add("/season", SeasonController.class);
		me.add("/data", DataController.class);
		me.add("/league", LeagueController.class);
		me.add("/team", TeamController.class);
		me.add("/banner", BannerController.class);
		me.add("/esoterica", EsotericaController.class);
		me.add("/attention", AttentionController.class);
		me.add("/update", UpdateController.class);
		me.add("/ranking", RankingController.class);
		me.add("/test", TestController.class);
		me.add("/im", MessageController.class);
	}

	@Override
	public void configPlugin(Plugins me) {
		Map<String, String> config = WorkingResourceUtil.loadPropertFile("jdbc.properties");
		DruidPlugin druidPlugin = new DruidPlugin(config.get("jdbcUrl"),
				config.get("user"), config.get("password"),
				config.get("driverClass"), "stat");
		me.add(druidPlugin);

		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
		arp.setContainerFactory(new IContainerFactory() {

			public Map<String, Object> getAttrsMap() {
				return new LinkedHashMap<String, Object>();
			}

			public Map<String, Object> getColumnsMap() {
				return new LinkedHashMap<String, Object>();
			}

			public Set<String> getModifyFlagSet() {
				return new LinkedHashSet<String>();
			}
		});
		me.add(arp);

		EventPlugin eventPlugin;
		eventPlugin = new EventPlugin(3);

		me.add(eventPlugin);

	}

	@Override
	public void afterJFinalStart() {
		// TODO Auto-generated method stub
		super.afterJFinalStart();
		Startup.start();
	}

	@Override
	public void configInterceptor(Interceptors me) {

	}

	@Override
	public void configHandler(Handlers me) {

	}

	@Override
	public void beforeJFinalStop() {
		Startup.stop();
		super.beforeJFinalStop();
	}

}
