<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="ResetPwd.aspx.cs" Inherits="IMAccount.ResetPwd" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
    
    &nbsp;&nbsp;
    
    &nbsp;
        <br />
&nbsp;账户&nbsp;&nbsp;&nbsp;&nbsp; 
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtUid" runat="server"  Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Label ID="Label2" runat="server" Text="旧密码"></asp:Label>
&nbsp;&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtCurrentPwd" runat="server"  Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Label ID="Label3" runat="server" Text="新密码"></asp:Label>
&nbsp;&nbsp;&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtNewPwd" runat="server"  Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Label ID="Label4" runat="server" Text="确认新密码"></asp:Label>
&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtConfirmNewPwd" runat="server"  Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Button ID="btnResetPwd" runat="server" Text="重置密码"  Height="35px" Width="500px" OnClick="btnResetPwd_Click" />
    
    </div>
    </form>
</body>
</html>
