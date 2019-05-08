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
        String data = "{";
        data += "\"build_name\":"+"\""+build.getProject().getName()+"\",";
	data += "\"build_number\":"+"\""+build.getNumber()+"\",";
	data += "\"build_status\":"+"\""+getResultAsString(build.getResult())+"\",";
	data += "\"roomNumber\":"+"\""+roomNumber+"\",";
	data += "\"user_id\":"+"\""+userID+"\"}";
		
	if(sender.send(data))
		listener.getLogger().println("Sending Success!");
	else
		listener.getLogger().println("Sending failed!");

        listener.getLogger().println("Hello, " + roomNumber + "!");
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
