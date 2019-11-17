<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="Reg.aspx.cs" Inherits="IMAccount.Manage" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title></title>
</head>
<body>
    <form id="form1" runat="server">
    <div>
    
    &nbsp;
        <br />
&nbsp;<asp:Label ID="Label1" runat="server" Text="昵称"></asp:Label>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtAlias" runat="server" Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Label ID="Label2" runat="server" Text="密码"></asp:Label>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtPwd" runat="server" Height="35px" Width="500px"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Label ID="Label3" runat="server" Text="确认密码"></asp:Label>
&nbsp;&nbsp;
        <br />
        <br />
&nbsp;<asp:TextBox ID="txtConfirmPwd" Height="35px" Width="500px" runat="server"></asp:TextBox>
&nbsp;<br />
        <br />
&nbsp;<asp:Button ID="btnReg" runat="server" Height="35px" Text="注册" OnClick ="btnReg_Click" Width="500px" />
    
    </div>
    </form>
</body>
</html>
