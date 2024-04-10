<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="suggestSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestJspBean" />
<% String strContent = suggestSuggest.getWorkflowTaskForm( request ); %>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
