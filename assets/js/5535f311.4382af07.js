"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[12],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>A});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function l(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var u=r.createContext({}),s=function(e){var t=r.useContext(u),n=t;return e&&(n="function"==typeof e?e(t):l(l({},t),e)),n},p=function(e){var t=s(e.components);return r.createElement(u.Provider,{value:t},e.children)},c="mdxType",d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,i=e.originalType,u=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),c=s(n),m=a,A=c["".concat(u,".").concat(m)]||c[m]||d[m]||i;return n?r.createElement(A,l(l({ref:t},p),{},{components:n})):r.createElement(A,l({ref:t},p))}));function A(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=n.length,l=new Array(i);l[0]=m;var o={};for(var u in t)hasOwnProperty.call(t,u)&&(o[u]=t[u]);o.originalType=e,o[c]="string"==typeof e?e:a,l[1]=o;for(var s=2;s<i;s++)l[s]=n[s];return r.createElement.apply(null,l)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5162:(e,t,n)=>{n.d(t,{Z:()=>l});var r=n(7294),a=n(6010);const i={tabItem:"tabItem_Ymn6"};function l(e){let{children:t,hidden:n,className:l}=e;return r.createElement("div",{role:"tabpanel",className:(0,a.Z)(i.tabItem,l),hidden:n},t)}},4866:(e,t,n)=>{n.d(t,{Z:()=>h});var r=n(7462),a=n(7294),i=n(6010),l=n(2466),o=n(6550),u=n(1980),s=n(7392),p=n(12);function c(e){return function(e){return a.Children.map(e,(e=>{if(!e||(0,a.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}(e).map((e=>{let{props:{value:t,label:n,attributes:r,default:a}}=e;return{value:t,label:n,attributes:r,default:a}}))}function d(e){const{values:t,children:n}=e;return(0,a.useMemo)((()=>{const e=t??c(n);return function(e){const t=(0,s.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,n])}function m(e){let{value:t,tabValues:n}=e;return n.some((e=>e.value===t))}function A(e){let{queryString:t=!1,groupId:n}=e;const r=(0,o.k6)(),i=function(e){let{queryString:t=!1,groupId:n}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!n)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return n??null}({queryString:t,groupId:n});return[(0,u._X)(i),(0,a.useCallback)((e=>{if(!i)return;const t=new URLSearchParams(r.location.search);t.set(i,e),r.replace({...r.location,search:t.toString()})}),[i,r])]}function b(e){const{defaultValue:t,queryString:n=!1,groupId:r}=e,i=d(e),[l,o]=(0,a.useState)((()=>function(e){let{defaultValue:t,tabValues:n}=e;if(0===n.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!m({value:t,tabValues:n}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${n.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const r=n.find((e=>e.default))??n[0];if(!r)throw new Error("Unexpected error: 0 tabValues");return r.value}({defaultValue:t,tabValues:i}))),[u,s]=A({queryString:n,groupId:r}),[c,b]=function(e){let{groupId:t}=e;const n=function(e){return e?`docusaurus.tab.${e}`:null}(t),[r,i]=(0,p.Nk)(n);return[r,(0,a.useCallback)((e=>{n&&i.set(e)}),[n,i])]}({groupId:r}),v=(()=>{const e=u??c;return m({value:e,tabValues:i})?e:null})();(0,a.useLayoutEffect)((()=>{v&&o(v)}),[v]);return{selectedValue:l,selectValue:(0,a.useCallback)((e=>{if(!m({value:e,tabValues:i}))throw new Error(`Can't select invalid tab value=${e}`);o(e),s(e),b(e)}),[s,b,i]),tabValues:i}}var v=n(2389);const f={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function B(e){let{className:t,block:n,selectedValue:o,selectValue:u,tabValues:s}=e;const p=[],{blockElementScrollPositionUntilNextRender:c}=(0,l.o5)(),d=e=>{const t=e.currentTarget,n=p.indexOf(t),r=s[n].value;r!==o&&(c(t),u(r))},m=e=>{let t=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":{const n=p.indexOf(e.currentTarget)+1;t=p[n]??p[0];break}case"ArrowLeft":{const n=p.indexOf(e.currentTarget)-1;t=p[n]??p[p.length-1];break}}t?.focus()};return a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,i.Z)("tabs",{"tabs--block":n},t)},s.map((e=>{let{value:t,label:n,attributes:l}=e;return a.createElement("li",(0,r.Z)({role:"tab",tabIndex:o===t?0:-1,"aria-selected":o===t,key:t,ref:e=>p.push(e),onKeyDown:m,onClick:d},l,{className:(0,i.Z)("tabs__item",f.tabItem,l?.className,{"tabs__item--active":o===t})}),n??t)})))}function k(e){let{lazy:t,children:n,selectedValue:r}=e;const i=(Array.isArray(n)?n:[n]).filter(Boolean);if(t){const e=i.find((e=>e.props.value===r));return e?(0,a.cloneElement)(e,{className:"margin-top--md"}):null}return a.createElement("div",{className:"margin-top--md"},i.map(((e,t)=>(0,a.cloneElement)(e,{key:t,hidden:e.props.value!==r}))))}function g(e){const t=b(e);return a.createElement("div",{className:(0,i.Z)("tabs-container",f.tabList)},a.createElement(B,(0,r.Z)({},e,t)),a.createElement(k,(0,r.Z)({},e,t)))}function h(e){const t=(0,v.Z)();return a.createElement(g,(0,r.Z)({key:String(t)},e))}},3825:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>u,default:()=>A,frontMatter:()=>o,metadata:()=>s,toc:()=>c});var r=n(7462),a=(n(7294),n(3905)),i=n(4866),l=n(5162);const o={title:"\u4f7f\u7528API",sidebar_position:1},u=void 0,s={unversionedId:"quick-start/api",id:"quick-start/api",title:"\u4f7f\u7528API",description:"API\u6a21\u5757\u662f\u72ec\u7acb\u7684\u3001\u591a\u5e73\u53f0\u7684\uff0c\u4f60\u53ef\u4ee5\u5355\u72ec\u4f7f\u7528\u5b83\u4f5c\u4e3a QQ\u9891\u9053API \u7684\u5c01\u88c5\u5e93\u3002",source:"@site/docs/quick-start/api.md",sourceDirName:"quick-start",slug:"/quick-start/api",permalink:"/simbot-component-qq-guild/docs/quick-start/api",draft:!1,editUrl:"https://github.com/simple-robot/simbot-component-qq-guild/tree/dev/main/website/docs/quick-start/api.md",tags:[],version:"current",lastUpdatedAt:1682001335,formattedLastUpdatedAt:"2023\u5e744\u670820\u65e5",sidebarPosition:1,frontMatter:{title:"\u4f7f\u7528API",sidebar_position:1},sidebar:"tutorialSidebar",previous:{title:"\u5feb\u901f\u5f00\u59cb",permalink:"/simbot-component-qq-guild/docs/quick-start/"},next:{title:"\u4f7f\u7528\u6807\u51c6\u5e93",permalink:"/simbot-component-qq-guild/docs/quick-start/stdlib"}},p={},c=[{value:"\u5b89\u88c5",id:"\u5b89\u88c5",level:2},{value:"\u4f7f\u7528",id:"\u4f7f\u7528",level:2},{value:"\u83b7\u53d6\u7528\u6237\u9891\u9053\u670d\u52a1\u5668\u5217\u8868",id:"\u83b7\u53d6\u7528\u6237\u9891\u9053\u670d\u52a1\u5668\u5217\u8868",level:3}],d={toc:c},m="wrapper";function A(e){let{components:t,...n}=e;return(0,a.kt)(m,(0,r.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"API\u6a21\u5757\u662f\u72ec\u7acb\u7684\u3001\u591a\u5e73\u53f0\u7684\uff0c\u4f60\u53ef\u4ee5\u5355\u72ec\u4f7f\u7528\u5b83\u4f5c\u4e3a ",(0,a.kt)("a",{parentName:"p",href:"https://bot.q.qq.com/wiki/develop/api/"},"QQ\u9891\u9053API")," \u7684\u5c01\u88c5\u5e93\u3002"),(0,a.kt)("admonition",{title:"\u7248\u672c?",type:"info"},(0,a.kt)("p",{parentName:"admonition"},"\u7248\u672c\u53ef\u524d\u5f80 ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/simple-robot/simbot-component-qq-guild/releases"},(0,a.kt)("strong",{parentName:"a"},"Releases"))," \u67e5\u770b\u5e76\u9009\u62e9\u3002")),(0,a.kt)("h2",{id:"\u5b89\u88c5"},"\u5b89\u88c5"),(0,a.kt)(i.Z,{groupId:"use-dependency",mdxType:"Tabs"},(0,a.kt)(l.Z,{value:"Gradle Kotlin DSL",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'// \u4e0d\u8981\u5fd8\u8bb0\u4f7f\u7528 Gradle \u7684 kotlin \u63d2\u4ef6\u6765\u5141\u8bb8\u81ea\u52a8\u9009\u62e9\u5bf9\u5e94\u5e73\u53f0\uff0c\u6bd4\u5982JVM\u6216JS\u7b49\u3002\n\nimplementation("love.forte.simbot.component:simbot-component-qq-gulid-api:$VERSION") // \u7248\u672c\u53c2\u8003\u524d\u6587\u6240\u8ff0\u7684 Releases\n'))),(0,a.kt)(l.Z,{value:"Gradle Groovy",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-groovy"},"// \u4e0d\u8981\u5fd8\u8bb0\u4f7f\u7528 Gradle \u7684 kotlin \u63d2\u4ef6\u6765\u5141\u8bb8\u81ea\u52a8\u9009\u62e9\u5bf9\u5e94\u5e73\u53f0\uff0c\u6bd4\u5982JVM\u6216JS\u7b49\u3002\n\nimplementation 'love.forte.simbot.component:simbot-component-qq-gulid-api:$VERSION' // \u7248\u672c\u53c2\u8003\u524d\u6587\u6240\u8ff0\u7684 Releases\n"))),(0,a.kt)(l.Z,{value:"Maven",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-xml"},"<dependency>\n    <groupId>love.forte.simbot.component</groupId>\n    \x3c!-- \u5728Maven\u4e2d\u4f7f\u7528 '-jvm' \u540e\u7f00\u6765\u9009\u62e9\u4f7f\u7528JVM\u5e73\u53f0\u5e93 --\x3e\n    <artifactId>simbot-component-qq-guild-api-jvm</artifactId>\n    \x3c!-- \u7248\u672c\u53c2\u8003\u524d\u6587\u6240\u8ff0\u7684 Releases --\x3e\n    <version>${VERSION}</version>\n</dependency>\n")))),(0,a.kt)("h2",{id:"\u4f7f\u7528"},"\u4f7f\u7528"),(0,a.kt)("admonition",{title:"\u592a\u591a\u4e86",type:"tip"},(0,a.kt)("p",{parentName:"admonition"},"\u6211\u4eec\u4e0d\u4f1a\u5728\u6b64\u5904\u4e00\u4e00\u5217\u4e3e\u6240\u6709\u7684API\u505a\u6f14\u793a\uff0c\u8fd9\u4e0d\u592a\u73b0\u5b9e\u3002\n\u6240\u6709\u7684API\u90fd\u5728\u5305\u8def\u5f84 ",(0,a.kt)("inlineCode",{parentName:"p"},"love.forte.simbot.qguild.api")," \u4e0b\uff0c\u4f60\u53ef\u4ee5\u901a\u8fc7 ",(0,a.kt)("a",{parentName:"p",href:"https://docs.simbot.forte.love/"},"API\u6587\u6863")," \u6216\u67e5\u9605\u6e90\u7801\u7684\u65b9\u5f0f\u6765\u5bfb\u627e\u4f60\u6240\u9700\u8981\u7684API\u3002"),(0,a.kt)("p",{parentName:"admonition"},"API\u5305\u88c5\u7c7b\u7684\u547d\u540d\u4e5f\u5b58\u5728\u4e00\u5b9a\u7684\u89c4\u5f8b\uff0c\u6bd4\u5982\u4e00\u4e2a ",(0,a.kt)("inlineCode",{parentName:"p"},"\u83b7\u53d6\u67d0\u5217\u8868")," \u7684API\u901a\u5e38\u4f1a\u88ab\u547d\u540d\u4e3a ",(0,a.kt)("inlineCode",{parentName:"p"},"GetXxxListApi"),"\u3002"),(0,a.kt)("p",{parentName:"admonition"},"\u4e0b\u6587\u4f1a\u9009\u62e9\u4e00\u5c0f\u90e8\u5206API\u6765\u505a\u793a\u4f8b\u3002")),(0,a.kt)("h3",{id:"\u83b7\u53d6\u7528\u6237\u9891\u9053\u670d\u52a1\u5668\u5217\u8868"},"\u83b7\u53d6\u7528\u6237\u9891\u9053\u670d\u52a1\u5668\u5217\u8868"),(0,a.kt)("p",null,"\u4ee5 ",(0,a.kt)("a",{parentName:"p",href:"https://bot.q.qq.com/wiki/develop/api/openapi/user/guilds.html"},"\u83b7\u53d6\u7528\u6237\uff08BOT\uff09\u9891\u9053\u670d\u52a1\u5668\u5217\u8868")," \u4e3a\u4f8b\u3002"),(0,a.kt)(i.Z,{groupId:"code",mdxType:"Tabs"},(0,a.kt)(l.Z,{value:"Kotlin",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'// \u51c6\u5907\u53c2\u6570\n// \u7528\u4e8e\u8bf7\u6c42\u7684token\nval token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"\n// \u7528\u4e8e\u8bf7\u6c42\u7684 Ktor HttpClient\uff0c\u5982\u6709\u5fc5\u8981\u5219\u9700\u8981\u81ea\u884c\u5f15\u5165\u5e76\u9009\u62e9\u9700\u8981\u4f7f\u7528\u7684\u5f15\u64ce\u3002\u53c2\u8003\uff1ahttps://ktor.io/docs/http-client-engines.html\nval client = HttpClient()\n// \u9700\u8981\u8bf7\u6c42\u7684\u73af\u5883\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u6bd4\u5982\u6b63\u5f0f\u73af\u5883\u6216\u6c99\u7bb1\u73af\u5883\uff0c\u4ea6\u6216\u662f\u67d0\u4e2a\u81ea\u5df1\u5b9a\u4e49\u4ee3\u7406\u7684\u7b2c\u4e09\u65b9\u73af\u5883\n// \u53ef\u4ee5\u901a\u8fc7 QQGuild \u5f97\u5230\u4e00\u4e9b\u9884\u5b9a\u4e49\u7684\u5e38\u91cf\u4fe1\u606f\nval server = QQGuild.SANDBOX_URL\n\n// \u4f7f\u7528 GetBotGuildListApi \u83b7\u53d6\u9891\u9053\u5217\u8868\n// \u521b\u5efa\u4e86\u4e00\u4e2a\u53c2\u6570 limit=100 \u7684 GetBotGuildListApi\uff0c\u5e76\u4f7f\u7528\u4e0a\u8ff0\u51c6\u5907\u597d\u7684\u53c2\u6570\u8fdb\u884c\u8bf7\u6c42\u3002\nval list: List<SimpleGuild> = GetBotGuildListApi.create(limit = 100).request(client, server, token)\n\nlist.forEach { ... }\n')),(0,a.kt)("p",null,"\u4e5f\u53ef\u4ee5\u901a\u8fc7\u989d\u5916\u7684\u6269\u5c55\u51fd\u6570\u6765\u83b7\u5f97\u4e00\u4e2a",(0,a.kt)("strong",{parentName:"p"},"\u5168\u91cf\u6570\u636e"),"\u7684\u6570\u636e\u6d41\u3002"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'// \u51c6\u5907\u53c2\u6570\n// \u7528\u4e8e\u8bf7\u6c42\u7684token\nval token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB"\n// \u7528\u4e8e\u8bf7\u6c42\u7684 Ktor HttpClient\uff0c\u5982\u6709\u5fc5\u8981\u5219\u9700\u8981\u81ea\u884c\u5f15\u5165\u5e76\u9009\u62e9\u9700\u8981\u4f7f\u7528\u7684\u5f15\u64ce\u3002\u53c2\u8003\uff1ahttps://ktor.io/docs/http-client-engines.html\nval client = HttpClient()\n// \u9700\u8981\u8bf7\u6c42\u7684\u73af\u5883\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u6bd4\u5982\u6b63\u5f0f\u73af\u5883\u6216\u6c99\u7bb1\u73af\u5883\uff0c\u4ea6\u6216\u662f\u67d0\u4e2a\u81ea\u5df1\u5b9a\u4e49\u4ee3\u7406\u7684\u7b2c\u4e09\u65b9\u73af\u5883\n// \u53ef\u4ee5\u901a\u8fc7 QQGuild \u5f97\u5230\u4e00\u4e9b\u9884\u5b9a\u4e49\u7684\u5e38\u91cf\u4fe1\u606f\nval server = QQGuild.SANDBOX_URL\n\n// \u4f7f\u7528 GetBotGuildListApi \u83b7\u53d6\u9891\u9053\u5217\u8868\n// \u521b\u5efa\u4e86\u4e00\u4e2a\u6bcf\u9875\u6570\u636e\u7684\u6570\u636e\u91cf\u90fd\u4e3a 100 \u7684\u5168\u91cf\u6570\u636e\u6d41\uff0c\u6bcf\u4e00\u9875\u90fd\u4f7f\u7528\u4e0a\u8ff0\u51c6\u5907\u597d\u7684\u53c2\u6570\u8fdb\u884c\u8bf7\u6c42\u3002\nval guildFlow: Flow<SimpleGuild> = GetBotGuildListApi.createFlow(batch = 100) { request(client, QQGuild.SANDBOX_URL, token) }\nguildFlow.collect { guild ->\n    // ...\n}\n'))),(0,a.kt)(l.Z,{value:"Java",label:"Java Blocking",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-java"},'// \u51c6\u5907\u53c2\u6570\n// \u7528\u4e8e\u8bf7\u6c42\u7684token\nString token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";\n// \u7528\u4e8e\u8bf7\u6c42\u7684 Ktor HttpClient\uff0c\u5982\u6709\u5fc5\u8981\u5219\u9700\u8981\u81ea\u884c\u5f15\u5165\u5e76\u9009\u62e9\u9700\u8981\u4f7f\u7528\u7684\u5f15\u64ce\u3002\u53c2\u8003\uff1ahttps://ktor.io/docs/http-client-engines.html\nHttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);\n// \u6216\u8005\u901a\u8fc7jvm\u5e73\u53f0\u5e93\u63d0\u4f9b\u7684\u5de5\u5177\u7c7b\u6765\u6784\u5efa\u4e00\u4e2a\u9ed8\u8ba4\u7684 client\u3002\uff08\u9700\u8981\u73af\u5883\u4e2d\u5b58\u5728\u4e00\u79cd\u5f15\u64ce\uff09\nHttpClient newClient = ApiRequestUtil.newHttpClient();\n// \u9700\u8981\u8bf7\u6c42\u7684\u73af\u5883\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u6bd4\u5982\u6b63\u5f0f\u73af\u5883\u6216\u6c99\u7bb1\u73af\u5883\uff0c\u4ea6\u6216\u662f\u67d0\u4e2a\u81ea\u5df1\u5b9a\u4e49\u4ee3\u7406\u7684\u7b2c\u4e09\u65b9\u73af\u5883\n// \u53ef\u4ee5\u901a\u8fc7 QQGuild \u5f97\u5230\u4e00\u4e9b\u9884\u5b9a\u4e49\u7684\u5e38\u91cf\u4fe1\u606f\nUrl server = QQGuild.SANDBOX_URL;\n\n// \u4f7f\u7528 GetBotGuildListApi \u83b7\u53d6\u9891\u9053\u5217\u8868,\n// \u521b\u5efa\u4e86\u4e00\u4e2a limit = 100 \u7684 GetBotGuildListApi\nGetBotGuildListApi api = GetBotGuildListApi.create(100);\n\n// \u53d1\u8d77\u8bf7\u6c42\u5e76\u5f97\u5230\u7ed3\u679c\nList<? extends SimpleGuild> guildList = api.doRequestBlocking(client, server, token);\n\nfor (SimpleGuild guild : guildList) {\n    // ...\n}\n'))),(0,a.kt)(l.Z,{value:"Java Async",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-java"},'// \u51c6\u5907\u53c2\u6570\n// \u7528\u4e8e\u8bf7\u6c42\u7684token\nString token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";\n// \u7528\u4e8e\u8bf7\u6c42\u7684 Ktor HttpClient\uff0c\u5982\u6709\u5fc5\u8981\u5219\u9700\u8981\u81ea\u884c\u5f15\u5165\u5e76\u9009\u62e9\u9700\u8981\u4f7f\u7528\u7684\u5f15\u64ce\u3002\u53c2\u8003\uff1ahttps://ktor.io/docs/http-client-engines.html\nHttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);\n// \u6216\u8005\u901a\u8fc7jvm\u5e73\u53f0\u5e93\u63d0\u4f9b\u7684\u5de5\u5177\u7c7b\u6765\u6784\u5efa\u4e00\u4e2a\u9ed8\u8ba4\u7684 client\u3002\uff08\u9700\u8981\u73af\u5883\u4e2d\u5b58\u5728\u4e00\u79cd\u5f15\u64ce\uff09\nHttpClient newClient = ApiRequestUtil.newHttpClient();\n// \u9700\u8981\u8bf7\u6c42\u7684\u73af\u5883\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u6bd4\u5982\u6b63\u5f0f\u73af\u5883\u6216\u6c99\u7bb1\u73af\u5883\uff0c\u4ea6\u6216\u662f\u67d0\u4e2a\u81ea\u5df1\u5b9a\u4e49\u4ee3\u7406\u7684\u7b2c\u4e09\u65b9\u73af\u5883\n// \u53ef\u4ee5\u901a\u8fc7 QQGuild \u5f97\u5230\u4e00\u4e9b\u9884\u5b9a\u4e49\u7684\u5e38\u91cf\u4fe1\u606f\nUrl server = QQGuild.SANDBOX_URL;\n\n// \u4f7f\u7528 GetBotGuildListApi \u83b7\u53d6\u9891\u9053\u5217\u8868,\n// \u521b\u5efa\u4e86\u4e00\u4e2a limit = 100 \u7684 GetBotGuildListApi\nGetBotGuildListApi api = GetBotGuildListApi.create(100);\n\n// \u53d1\u8d77\u8bf7\u6c42\u5e76\u5f97\u5230 Future \u7ed3\u679c\napi.doRequestAsync(client, server, token).thenAccept(guildList -> {\n    for (SimpleGuild guild : guildList) {\n        // ...\n    }\n});\n'))),(0,a.kt)(l.Z,{value:"Java Reactive",mdxType:"TabItem"},(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-java"},'// \u51c6\u5907\u53c2\u6570\n// \u7528\u4e8e\u8bf7\u6c42\u7684token\nString token = "Bot 123456789.ABABABABABABABABABABABABABABABABAB";\n// \u7528\u4e8e\u8bf7\u6c42\u7684 Ktor HttpClient\uff0c\u5982\u6709\u5fc5\u8981\u5219\u9700\u8981\u81ea\u884c\u5f15\u5165\u5e76\u9009\u62e9\u9700\u8981\u4f7f\u7528\u7684\u5f15\u64ce\u3002\u53c2\u8003\uff1ahttps://ktor.io/docs/http-client-engines.html\nHttpClient client = HttpClientJvmKt.HttpClient(($1) -> Unit.INSTANCE);\n// \u6216\u8005\u901a\u8fc7jvm\u5e73\u53f0\u5e93\u63d0\u4f9b\u7684\u5de5\u5177\u7c7b\u6765\u6784\u5efa\u4e00\u4e2a\u9ed8\u8ba4\u7684 client\u3002\uff08\u9700\u8981\u73af\u5883\u4e2d\u5b58\u5728\u4e00\u79cd\u5f15\u64ce\uff09\nHttpClient newClient = ApiRequestUtil.newHttpClient();\n// \u9700\u8981\u8bf7\u6c42\u7684\u73af\u5883\u7684\u670d\u52a1\u5668\u5730\u5740\uff0c\u6bd4\u5982\u6b63\u5f0f\u73af\u5883\u6216\u6c99\u7bb1\u73af\u5883\uff0c\u4ea6\u6216\u662f\u67d0\u4e2a\u81ea\u5df1\u5b9a\u4e49\u4ee3\u7406\u7684\u7b2c\u4e09\u65b9\u73af\u5883\n// \u53ef\u4ee5\u901a\u8fc7 QQGuild \u5f97\u5230\u4e00\u4e9b\u9884\u5b9a\u4e49\u7684\u5e38\u91cf\u4fe1\u606f\nUrl server = QQGuild.SANDBOX_URL;\n\n// \u4f7f\u7528 GetBotGuildListApi \u83b7\u53d6\u9891\u9053\u5217\u8868,\n// \u521b\u5efa\u4e86\u4e00\u4e2a limit = 100 \u7684 GetBotGuildListApi\nGetBotGuildListApi api = GetBotGuildListApi.create(100);\n\n// \u53d1\u8d77\u8bf7\u6c42\u5e76\u5f97\u5230\u54cd\u5e94\u5f0f\u7ed3\u679c\nFlux<? extends SimpleGuild> guildFlux = Mono.fromCompletionStage(api.doRequestAsync(client, server, token))\n        .flatMapIterable(Function.identity());\n\nreturn guildFlux;\n')))))}A.isMDXComponent=!0}}]);