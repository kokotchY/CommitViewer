package be.rvponp.build.util;

import java.net.URL;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import com.atlassian.jira.rpc.exception.RemoteAuthenticationException;
import jira.rpc.soap.jirasoapservice_v2.JiraSoapService;
import jira.rpc.soap.jirasoapservice_v2.JiraSoapServiceService;
import jira.rpc.soap.jirasoapservice_v2.JiraSoapServiceServiceLocator;
import org.apache.log4j.Logger;

/**
 * This represents a SOAP session with JIRA including that state of being logged in or not
 */
public class SOAPSession
{
    private JiraSoapServiceService jiraSoapServiceLocator;
    private JiraSoapService jiraSoapService;
    private String token;
    private static final Logger log = Logger.getLogger(SOAPSession.class);

    public SOAPSession(URL webServicePort)
    {
        jiraSoapServiceLocator = new JiraSoapServiceServiceLocator();
        try
        {
            if (webServicePort == null)
            {
                jiraSoapService = jiraSoapServiceLocator.getJirasoapserviceV2();
            }
            else
            {
                jiraSoapService = jiraSoapServiceLocator.getJirasoapserviceV2(webServicePort);
                log.info("SOAP Session service endpoint at " + webServicePort.toExternalForm());
            }
        }
        catch (ServiceException e)
        {
            throw new RuntimeException("ServiceException during SOAPClient contruction", e);
        }
    }
    public SOAPSession()
    {
        this(null);
    }
    public void connect(String userName, String password) throws RemoteException {
        log.info("\tConnnecting via SOAP as : " + userName);
        token = getJiraSoapService().login(userName, password);
        log.info("\tConnected");
    }
    public String getAuthenticationToken()
    {
        return token;
    }
    public JiraSoapService getJiraSoapService()
    {
        return jiraSoapService;
    }
    public JiraSoapServiceService getJiraSoapServiceLocator()
    {
        return jiraSoapServiceLocator;
    }
}
