<!DOCTYPE html>
<%@ page import="org.apache.shiro.SecurityUtils" %><%
    if (SecurityUtils.getSubject().isAuthenticated()) {
        response.sendRedirect(request.getContextPath());
    }
%>
<html>
    <head>
        <meta charset="UTF-8">
        <title>tudo</title>
        <link href="https://fonts.googleapis.com/css?family=Noto+Sans:400,700" rel="stylesheet">
        <link rel="stylesheet" href="style.css">
        <link rel="icon" type="image/png" sizes="32x32" href="../favicon-32x32.png">
        <link rel="icon" type="image/png" sizes="16x16" href="../favicon-16x16.png">
        <script>
            "use strict";var sjcl={cipher:{},hash:{},keyexchange:{},mode:{},misc:{},codec:{},exception:{corrupt:function(a){this.toString=function(){return"CORRUPT: "+this.message};this.message=a},invalid:function(a){this.toString=function(){return"INVALID: "+this.message};this.message=a},bug:function(a){this.toString=function(){return"BUG: "+this.message};this.message=a},notReady:function(a){this.toString=function(){return"NOT READY: "+this.message};this.message=a}}};
            sjcl.bitArray={bitSlice:function(a,b,c){a=sjcl.bitArray.h(a.slice(b/32),32-(b&31)).slice(1);return void 0===c?a:sjcl.bitArray.clamp(a,c-b)},extract:function(a,b,c){var d=Math.floor(-b-c&31);return((b+c-1^b)&-32?a[b/32|0]<<32-d^a[b/32+1|0]>>>d:a[b/32|0]>>>d)&(1<<c)-1},concat:function(a,b){if(0===a.length||0===b.length)return a.concat(b);var c=a[a.length-1],d=sjcl.bitArray.getPartial(c);return 32===d?a.concat(b):sjcl.bitArray.h(b,d,c|0,a.slice(0,a.length-1))},bitLength:function(a){var b=a.length;return 0===
                b?0:32*(b-1)+sjcl.bitArray.getPartial(a[b-1])},clamp:function(a,b){if(32*a.length<b)return a;a=a.slice(0,Math.ceil(b/32));var c=a.length;b=b&31;0<c&&b&&(a[c-1]=sjcl.bitArray.partial(b,a[c-1]&2147483648>>b-1,1));return a},partial:function(a,b,c){return 32===a?b:(c?b|0:b<<32-a)+0x10000000000*a},getPartial:function(a){return Math.round(a/0x10000000000)||32},equal:function(a,b){if(sjcl.bitArray.bitLength(a)!==sjcl.bitArray.bitLength(b))return!1;var c=0,d;for(d=0;d<a.length;d++)c|=a[d]^b[d];return 0===
                    c},h:function(a,b,c,d){var e;e=0;for(void 0===d&&(d=[]);32<=b;b-=32)d.push(c),c=0;if(0===b)return d.concat(a);for(e=0;e<a.length;e++)d.push(c|a[e]>>>b),c=a[e]<<32-b;e=a.length?a[a.length-1]:0;a=sjcl.bitArray.getPartial(e);d.push(sjcl.bitArray.partial(b+a&31,32<b+a?c:d.pop(),1));return d},i:function(a,b){return[a[0]^b[0],a[1]^b[1],a[2]^b[2],a[3]^b[3]]},byteswapM:function(a){var b,c;for(b=0;b<a.length;++b)c=a[b],a[b]=c>>>24|c>>>8&0xff00|(c&0xff00)<<8|c<<24;return a}};
            sjcl.codec.utf8String={fromBits:function(a){var b="",c=sjcl.bitArray.bitLength(a),d,e;for(d=0;d<c/8;d++)0===(d&3)&&(e=a[d/4]),b+=String.fromCharCode(e>>>8>>>8>>>8),e<<=8;return decodeURIComponent(escape(b))},toBits:function(a){a=unescape(encodeURIComponent(a));var b=[],c,d=0;for(c=0;c<a.length;c++)d=d<<8|a.charCodeAt(c),3===(c&3)&&(b.push(d),d=0);c&3&&b.push(sjcl.bitArray.partial(8*(c&3),d));return b}};
            sjcl.codec.hex={fromBits:function(a){var b="",c;for(c=0;c<a.length;c++)b+=((a[c]|0)+0xf00000000000).toString(16).substr(4);return b.substr(0,sjcl.bitArray.bitLength(a)/4)},toBits:function(a){var b,c=[],d;a=a.replace(/\s|0x/g,"");d=a.length;a=a+"00000000";for(b=0;b<a.length;b+=8)c.push(parseInt(a.substr(b,8),16)^0);return sjcl.bitArray.clamp(c,4*d)}};sjcl.hash.sha256=function(a){this.f[0]||p(this);a?(this.c=a.c.slice(0),this.b=a.b.slice(0),this.a=a.a):this.reset()};sjcl.hash.sha256.hash=function(a){return(new sjcl.hash.sha256).update(a).finalize()};
            sjcl.hash.sha256.prototype={blockSize:512,reset:function(){this.c=this.g.slice(0);this.b=[];this.a=0;return this},update:function(a){"string"===typeof a&&(a=sjcl.codec.utf8String.toBits(a));var b,c=this.b=sjcl.bitArray.concat(this.b,a);b=this.a;a=this.a=b+sjcl.bitArray.bitLength(a);if(0x1fffffffffffff<a)throw new sjcl.exception.invalid("Cannot hash more than 2^53 - 1 bits");if("undefined"!==typeof Uint32Array){var d=new Uint32Array(c),e=0;for(b=512+b-(512+b&0x1ff);b<=a;b+=512)t(this,d.subarray(16*e,
                    16*(e+1))),e+=1;c.splice(0,16*e)}else for(b=512+b-(512+b&0x1ff);b<=a;b+=512)t(this,c.splice(0,16));return this},finalize:function(){var a,b=this.b,c=this.c,b=sjcl.bitArray.concat(b,[sjcl.bitArray.partial(1,1)]);for(a=b.length+2;a&15;a++)b.push(0);b.push(Math.floor(this.a/0x100000000));for(b.push(this.a|0);b.length;)t(this,b.splice(0,16));this.reset();return c},g:[],f:[]};
            function t(a,b){var c,d,e,f=a.c,u=a.f,q=f[0],g=f[1],k=f[2],m=f[3],h=f[4],n=f[5],l=f[6],r=f[7];for(c=0;64>c;c++)16>c?d=b[c]:(d=b[c+1&15],e=b[c+14&15],d=b[c&15]=(d>>>7^d>>>18^d>>>3^d<<25^d<<14)+(e>>>17^e>>>19^e>>>10^e<<15^e<<13)+b[c&15]+b[c+9&15]|0),d=d+r+(h>>>6^h>>>11^h>>>25^h<<26^h<<21^h<<7)+(l^h&(n^l))+u[c],r=l,l=n,n=h,h=m+d|0,m=k,k=g,g=q,q=d+(g&k^m&(g^k))+(g>>>2^g>>>13^g>>>22^g<<30^g<<19^g<<10)|0;f[0]=f[0]+q|0;f[1]=f[1]+g|0;f[2]=f[2]+k|0;f[3]=f[3]+m|0;f[4]=f[4]+h|0;f[5]=f[5]+n|0;f[6]=f[6]+l|0;f[7]=
                f[7]+r|0}function p(a){function b(a){return 0x100000000*(a-Math.floor(a))|0}for(var c=0,d=2,e,f;64>c;d++){f=!0;for(e=2;e*e<=d;e++)if(0===d%e){f=!1;break}f&&(8>c&&(a.g[c]=b(Math.pow(d,.5))),a.f[c]=b(Math.pow(d,1/3)),c++)}}"undefined"!==typeof module&&module.exports&&(module.exports=sjcl);"function"===typeof define&&define([],function(){return sjcl});
        </script><!-- Stanford Javascript Crypto Library https://github.com/bitwiseshiftleft/sjcl/ -->
    </head>
<body>
<main>
    <div class="bg"></div>
    <div class="wrapper">
        <form class="login-form" id="loginForm" action="#" method="POST">
            <h1>Sign in</h1>
            <input type="text" id="username" name="username" placeholder="Username">
            <input type="password" id="passwordUnhashed" name="passwordUnhashed" placeholder="Password">
            <input type="hidden" id="password" name="password" value="">
            <span class="errormessage <% if(request.getAttribute("shiroLoginFailure")!=null){out.println("error");}%>">
                Wrong credentials! Please check your input and try again.
            </span>
            <button type="submit">Sign In</button>
            <p>Don't have an account yet? <a href="register.jsp">Create one now!</a></p>
        </form>
        <footer>
            <div class="copyright">
                &copy; 2018 Vanessa Speeth, Merlin Scholz
            </div>
            <div class="logo">
                tudo
            </div>
        </footer>
    </div>
</main>
<script>
    document.getElementById("loginForm").onsubmit = function(e){
        e.preventDefault();
        document.getElementById("password").value = sjcl.codec.hex.fromBits(sjcl.hash.sha256.hash(document.getElementById("passwordUnhashed").value));
        document.getElementById("passwordUnhashed").value = "";
        document.getElementById("loginForm").submit();
    }
</script>
</body>
</html>
