"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[775],{3905:(e,n,t)=>{t.d(n,{Zo:()=>s,kt:()=>f});var i=t(7294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function r(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);n&&(i=i.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,i)}return t}function l(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?r(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):r(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function a(e,n){if(null==e)return{};var t,i,o=function(e,n){if(null==e)return{};var t,i,o={},r=Object.keys(e);for(i=0;i<r.length;i++)t=r[i],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(i=0;i<r.length;i++)t=r[i],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var p=i.createContext({}),c=function(e){var n=i.useContext(p),t=n;return e&&(t="function"==typeof e?e(n):l(l({},n),e)),t},s=function(e){var n=c(e.components);return i.createElement(p.Provider,{value:n},e.children)},u="mdxType",m={inlineCode:"code",wrapper:function(e){var n=e.children;return i.createElement(i.Fragment,{},n)}},d=i.forwardRef((function(e,n){var t=e.components,o=e.mdxType,r=e.originalType,p=e.parentName,s=a(e,["components","mdxType","originalType","parentName"]),u=c(t),d=o,f=u["".concat(p,".").concat(d)]||u[d]||m[d]||r;return t?i.createElement(f,l(l({ref:n},s),{},{components:t})):i.createElement(f,l({ref:n},s))}));function f(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var r=t.length,l=new Array(r);l[0]=d;var a={};for(var p in n)hasOwnProperty.call(n,p)&&(a[p]=n[p]);a.originalType=e,a[u]="string"==typeof e?e:o,l[1]=a;for(var c=2;c<r;c++)l[c]=t[c];return i.createElement.apply(null,l)}return i.createElement.apply(null,t)}d.displayName="MDXCreateElement"},5647:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>p,contentTitle:()=>l,default:()=>m,frontMatter:()=>r,metadata:()=>a,toc:()=>c});var i=t(7462),o=(t(7294),t(3905));const r={title:"BOT\u914d\u7f6e\u6587\u4ef6",toc_max_heading_level:4},l=void 0,a={unversionedId:"bot-config/index",id:"bot-config/index",title:"BOT\u914d\u7f6e\u6587\u4ef6",description:"\u5f85\u65bd\u5de5",source:"@site/docs/bot-config/index.md",sourceDirName:"bot-config",slug:"/bot-config/",permalink:"/simbot-component-qq-guild/docs/bot-config/",draft:!1,editUrl:"https://github.com/simple-robot/simbot-component-qq-guild/tree/dev/main/website/docs/bot-config/index.md",tags:[],version:"current",lastUpdatedAt:1684376445,formattedLastUpdatedAt:"2023\u5e745\u670818\u65e5",frontMatter:{title:"BOT\u914d\u7f6e\u6587\u4ef6",toc_max_heading_level:4},sidebar:"tutorialSidebar",previous:{title:"\u4f7f\u7528SpringBoot",permalink:"/simbot-component-qq-guild/docs/quick-start/spring-boot"},next:{title:"\u8bba\u575b",permalink:"/simbot-component-qq-guild/docs/api/forum/"}},p={},c=[{value:"\u914d\u7f6e\u9879",id:"\u914d\u7f6e\u9879",level:2},{value:"component",id:"component",level:3},{value:"ticket",id:"ticket",level:3},{value:"config",id:"config",level:3},{value:"config.serverUrl",id:"configserverurl",level:4},{value:"config.shard",id:"configshard",level:4},{value:"config.intents",id:"configintents",level:4}],s={toc:c},u="wrapper";function m(e){let{components:n,...t}=e;return(0,o.kt)(u,(0,i.Z)({},s,t,{components:n,mdxType:"MDXLayout"}),(0,o.kt)("admonition",{title:"\u5f85\u65bd\u5de5",type:"caution"},(0,o.kt)("p",{parentName:"admonition"},"\u5f85\u65bd\u5de5")),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json",metastring:"title='xxx.bot.json'",title:"'xxx.bot.json'"},'{\n    "component": "simbot.qqguild",\n    "ticket": {\n        "appId": "APPID",\n        "token": "TOKEN",\n        "secret": "SECRET"\n    },\n    "config": {\n        "serverUrl": null,\n        "shard": {\n            "type": "full"\n        },\n        "intents": {\n            "type": "raw",\n            "intents": 1073741827\n        },\n        "timeout": {\n          "apiHttpRequestTimeoutMillis": null,\n          "apiHttpConnectTimeoutMillis": null,\n          "apiHttpSocketTimeoutMillis": null\n        },\n        "cache": {\n          "enable": true,\n          "transmit": {\n            "enable": true\n          }\n        },\n        "clientProperties": null\n    }\n}\n')),(0,o.kt)("admonition",{type:"info"},(0,o.kt)("p",{parentName:"admonition"},"\u6587\u4ef6\u914d\u7f6e\u7c7b\u7684\u5404\u5c5e\u6027\u5b9a\u4e49\u53ef\u53c2\u8003API\u6587\u6863: ",(0,o.kt)("a",{parentName:"p",href:"https://docs.simbot.forte.love/components/qq-guild/simbot-component-qq-guild-core-common/love.forte.simbot.component.qguild.config/-q-g-bot-file-configuration/index.html"},(0,o.kt)("inlineCode",{parentName:"a"},"QGBotFileConfiguration")))),(0,o.kt)("h2",{id:"\u914d\u7f6e\u9879"},"\u914d\u7f6e\u9879"),(0,o.kt)("h3",{id:"component"},"component"),(0,o.kt)("p",null,"\u56fa\u5b9a\u503c ",(0,o.kt)("inlineCode",{parentName:"p"},"simbot.qqguild"),"\uff0c",(0,o.kt)("strong",{parentName:"p"},"\u5fc5\u586b"),"\uff0c\u4ee3\u8868\u6b64\u914d\u7f6e\u6587\u4ef6\u4e3aQQ\u9891\u9053\u7ec4\u4ef6\u7684\u3002"),(0,o.kt)("h3",{id:"ticket"},"ticket"),(0,o.kt)("p",null,"bot\u7684\u7968\u636e\u4fe1\u606f\uff0c",(0,o.kt)("strong",{parentName:"p"},"\u5fc5\u586b"),"\u3002"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"appId"),": BotAppID"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"token"),": \u673a\u5668\u4eba\u4ee4\u724c"),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"secret"),": \u673a\u5668\u4eba\u5bc6\u94a5 (\u76ee\u524d\u6682\u65f6\u4e0d\u4f1a\u7528\u5230\uff0c\u53ef\u4ee5\u7528 ",(0,o.kt)("inlineCode",{parentName:"li"},'""')," \u4ee3\u66ff)")),(0,o.kt)("h3",{id:"config"},"config"),(0,o.kt)("p",null,"\u5176\u4ed6\u914d\u7f6e\uff0c\u53ef\u9009\uff0c\u9ed8\u8ba4\u4e3a ",(0,o.kt)("inlineCode",{parentName:"p"},"null"),"\u3002"),(0,o.kt)("h4",{id:"configserverurl"},"config.serverUrl"),(0,o.kt)("p",null,"\u5185\u90e8\u8fdb\u884cAPI\u8bf7\u6c42\u65f6\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u53c2\u8003",(0,o.kt)("a",{parentName:"p",href:"https://bot.q.qq.com/wiki/develop/api/"},"\u5b98\u65b9\u6587\u6863")),(0,o.kt)("p",null,"\u9ed8\u8ba4\u4e3a ",(0,o.kt)("inlineCode",{parentName:"p"},"null"),"\uff0c\u4e3a ",(0,o.kt)("inlineCode",{parentName:"p"},"null")," \u65f6\u4e3a\u6b63\u5f0f\u73af\u5883\uff0c\u53ef\u4f7f\u7528\u4e00\u4e2a\u56fa\u5b9a\u503c ",(0,o.kt)("inlineCode",{parentName:"p"},"SANDBOX")," \u4ee3\u8868\u4f7f\u7528\u6c99\u7bb1\u73af\u5883"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "config": {\n    "serverUrl": "SANDBOX"\n  }  \n}\n')),(0,o.kt)("p",null,"\u6216\u8005\u4f7f\u7528\u4e00\u4e2a\u5177\u4f53\u7684\u5176\u4ed6\u670d\u52a1\u5668\u5730\u5740"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "config": {\n    "serverUrl": "https://example.com"\n  }  \n}\n')),(0,o.kt)("h4",{id:"configshard"},"config.shard"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://bot.q.qq.com/wiki/develop/api/gateway/shard.html"},"\u5206\u7247\u4fe1\u606f"),"\uff0c\u9ed8\u8ba4\u4e3a ",(0,o.kt)("inlineCode",{parentName:"p"},"type=full"),"\uff0c\u5373\u4f7f\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"[0, 1]")," \u7684\u5206\u7247\u3002"),(0,o.kt)("p",null,"\u53ef\u4ee5\u4f7f\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"type=simple")," \u81ea\u5b9a\u4e49\u5206\u7247\uff1a"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "config": {\n    "shard": {\n      "type": "simple",\n      "value": 0,\n      "total": 2\n    }\n  }  \n}\n')),(0,o.kt)("h4",{id:"configintents"},"config.intents"),(0,o.kt)("p",null,(0,o.kt)("a",{parentName:"p",href:"https://bot.q.qq.com/wiki/develop/api/gateway/intents.html"},"\u8ba2\u9605\u7684\u4e8b\u4ef6"),"\uff0c\u9ed8\u8ba4\u60c5\u51b5\u4e0b\u8ba2\u9605\uff1a"),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"Guilds")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"GuildMembers")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"PublicGuildMessages"))),(0,o.kt)("p",null,"\u53ef\u901a\u8fc7 ",(0,o.kt)("inlineCode",{parentName:"p"},"type=raw")," \u6765\u76f4\u63a5\u6307\u5b9a\u4e00\u4e2a\u539f\u59cb\u7684\u8ba2\u9605\u6807\u8bb0\u7ed3\u679c\u503c\uff1a"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "config": {\n    "intents": {\n      "type": "raw",\n      "intents": 1073741827\n    }\n  }  \n}\n')),(0,o.kt)("p",null,"\u6216\u8005\u4f7f\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"type=nameBased")," \u901a\u8fc7\u6307\u5b9a\u540d\u79f0\uff08\u540d\u79f0\u9009\u62e9\u53c2\u8003 ",(0,o.kt)("inlineCode",{parentName:"p"},"EventIntents")," \u7c7b\u7684\u6240\u6709 ",(0,o.kt)("inlineCode",{parentName:"p"},"object")," \u7c7b\u578b\u7684\u5b57\u7c7b\u7c7b\u540d\uff09\uff1a"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-json"},'{\n  "config": {\n    "intents": {\n      "type": "nameBased",\n      "names": ["Guilds", "GuildMembers", "PublicGuildMessages"]\n    }\n  }  \n}\n')))}m.isMDXComponent=!0}}]);