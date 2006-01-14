<%@ taglib uri="/tlds/c.tld" prefix="c" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/struts-nested.tld" prefix="nested" %>
<%@ taglib uri="/tlds/displaytag-12.tld" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags/portal" prefix="portal" %>

<%
   String gotoUrl = null;
   if (request.getQueryString() != null && request.getQueryString().indexOf("channelUrl") >= 0) {
      gotoUrl = request.getQueryString().substring(request.getQueryString().indexOf("channelUrl")+11,request.getQueryString().length());
   } else if (request.getParameter("channelUrl") != null && request.getParameter("channelUrl").length() > 0) {
      gotoUrl = request.getParameter("channelUrl");
   }
   request.setAttribute("gotoUrl", gotoUrl);
%>

<portal:portalTop />
<portal:portalTabs selectedTab="${param.selectedTab}" />
<portal:portalBody  selectedTab="${param.selectedTab}" channelTitle="${param.channelTitle}" channelUrl="${gotoUrl}" />
<portal:portalBottom />
