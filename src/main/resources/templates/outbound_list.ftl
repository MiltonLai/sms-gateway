<#include "includes/overall_header.ftl">
<p>发送短信记录</p>
<table border="1">
  <tr>
    <td>Sender</td>
    <td>Recipient</td>
    <td>Content</td>
    <td>Date</td>
    <td>Status</td>
    <td>Note</td>
  </tr>
<#list vos as vo>
  <tr>
    <td>${vo.sender!""}</td>
    <td>${vo.recipient!""}</td>
    <td>${vo.content!""}</td>
    <td><#if vo.date??>${vo.date?string("yyyy-MM-dd HH:mm:ss")}<#else></#if></td>
    <td>${vo.status!""}</td>
    <td>${vo.note!""}</td>
  </tr>
</#list>
</table>
<#include "includes/overall_footer.ftl">