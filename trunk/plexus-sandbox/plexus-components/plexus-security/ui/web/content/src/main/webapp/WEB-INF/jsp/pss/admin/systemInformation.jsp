<%--
  ~ Copyright 2005-2006 The Codehaus.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ taglib prefix="ww" uri="/webwork"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <title>[Admin] System Information</title>
  <ww:head/>
</head>

<body>

<ul>
  <li><strong>Authentication</strong> : ${authentication}</li>
  <li><strong>Authorization</strong> : ${authorization}</li>
  <li><strong>User Management</strong> : ${userManagement}</li>
  <li><strong>Key Management</strong> : ${keyManagement}</li>
  <li><strong>Policy</strong> : ${policy}</li>
</ul>

</body>
</html>