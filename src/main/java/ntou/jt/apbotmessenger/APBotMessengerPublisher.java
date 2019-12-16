package ntou.jt.apbotmessenger;

import hudson.Extension;
import hudson.Launcher;
import hudson.util.FormValidation;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.tasks.test.AggregatedTestResultAction;
import hudson.tasks.junit.CaseResult;
import hudson.EnvVars;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundSetter;
import ntou.jt.apbotmessenger.EmitLog;

public class APBotMessengerPublisher extends Notifier {

    private final String roomNumber;
    private final String userID;
    private EmitLog sender = new EmitLog();
    @DataBoundConstructor
    public APBotMessengerPublisher(String roomNumber,String userID) {
		this.userID = userID;
		this.roomNumber = roomNumber;
    }
	//get the roomNumber
    public String getroomNumber() {
        return roomNumber;
    }
    public String getuserID() {
        return userID;
    }
    private String getResultAsString(Result result) {
        String retStr = "ONGOING";
        if (result != null) {
            retStr = result.toString();
       }
       return retStr;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
	//get project datas and push them to hubot
	// Generate message (JSON format)
        JSONObject obj = new JSONObject();
        obj.put("build_name", build.getProject().getName());
        obj.put("build_number", build.getNumber());
	obj.put("build_status", getResultAsString(build.getResult()));
	obj.put("roomNumber", roomNumber);
	obj.put("user_id", userID);
	JSONArray tempCase = new JSONArray();

	AggregatedTestResultAction resultAction = (AggregatedTestResultAction)build.getAggregatedTestResultAction();
	List failList = new ArrayList();
	try
	{
		failList = resultAction.getFailedTests();
		obj.put("fail_count", resultAction.getFailCount());
		obj.put("skip_count", resultAction.getSkipCount());
		obj.put("total_count", resultAction.getTotalCount());
	}
	catch (Exception e) 
	{
		obj.put("fail_count", 0);
		obj.put("skip_count", 0);
		obj.put("total_count", 0);
	}
	//put failCase into tempCase here
	for(int i = 0;i < failList.size();i++)
	{	
		CaseResult failCase = (CaseResult)failList.get(i);
		JSONObject fail_obj = new JSONObject();
		fail_obj.put("name", failCase.getName());
		fail_obj.put("out", failCase.getStdout());
		tempCase.put(fail_obj);
	}
	obj.put("fail_case", tempCase);

	
	final EnvVars env = build.getEnvironment(listener);
	String BUILD_URL = env.get("BUILD_URL");
	
	obj.put("build_url", BUILD_URL);

	listener.getLogger().println("Data : " + build.getRootDir().getAbsolutePath());
	listener.getLogger().println("Build_URL : " + BUILD_URL);
	
	if(sender.send(obj.toString()))
	    listener.getLogger().println("Sending Success!");
	else
	    listener.getLogger().println("Sending failed!");

	return true;
    }

	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
	
    @Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.BUILD;
	}
		


    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

	//check the form
        public FormValidation doCheckroomNumber(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error(Messages.APBotMessengerPublisher_DescriptorImpl_errors_missingName());
            if (value.length() < 4)
                return FormValidation.warning(Messages.APBotMessengerPublisher_DescriptorImpl_warnings_tooShort());
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.APBotMessengerPublisher_DescriptorImpl_DisplayName();
        }

    }

}
