"use strict";(self["webpackChunkscoring_system_front"]=self["webpackChunkscoring_system_front"]||[]).push([[939,963],{41:function(e,t,a){a.d(t,{AV:function(){return l},XD:function(){return o},_8:function(){return r},q6:function(){return s}});var n=a(5857);const o=(e,t)=>new Promise(((a,o)=>{(0,n.A)({method:"get",url:"/api/todolist",headers:{"Content-Type":"application/json"},params:{pageNum:e,pageSize:t}}).then((e=>{a(e)})).catch((e=>{o(e.response)}))})),r=()=>new Promise(((e,t)=>{(0,n.A)({method:"get",url:"/api/findAll",headers:{"Content-Type":"application/json"}}).then((t=>{e(t)})).catch((e=>{t(e.response)}))})),s=e=>new Promise(((t,a)=>{let o={userIds:e};(0,n.A)({method:"post",url:"/api/add/evaluated",headers:{"Content-Type":"application/json"},data:JSON.stringify(o)}).then((e=>{t(e)})).catch((e=>{a(e.response)}))})),l=()=>new Promise(((e,t)=>{(0,n.A)({method:"get",url:"/api/find/assessor",headers:{"Content-Type":"application/json"}}).then((t=>{e(t)})).catch((e=>{t(e.response)}))}))},5939:function(e,t,a){a.r(t),a.d(t,{default:function(){return d}});var n=a(7616),o=a(3e3),r=a(7963),s={__name:"TodoListAd",setup(e){return(e,t)=>((0,n.uX)(),(0,n.Wv)((0,o.R1)(r["default"])))}};const l=s;var d=l},7963:function(e,t,a){a.r(t),a.d(t,{default:function(){return A}});a(9138);var n=a(7616),o=a(5929),r=a(3e3),s=a(1516),l=a(4720),d=a(41);a(9314);const i={class:"todoList-container"},u={class:"left"},c={class:"todoTable"},p={key:0},g={key:0},k={key:1},m=["onClick"],v={key:2},y=["onClick"],h={class:"right"},f={style:{"margin-top":"10px"}};var b={__name:"TodoList",setup(e){(0,n.sV)((()=>{I(),D(),A(),window.addEventListener("resize",A),W()}));(0,n.hi)((()=>{window.removeEventListener("resize",A)}));const t=(0,r.KR)("60vh"),a=(0,l.rd)(),b=(0,r.Kh)({pageSize:11,current:1,showSizeChanger:!1,total:0,showTotal:(e,t)=>`总共 ${e} 条数据`}),w=(0,r.KR)([{title:"序号",dataIndex:"index",key:"index",width:"10%",align:"center"},{title:"任务类型",dataIndex:"taskTag",key:"taskTag",width:"15%",align:"center"},{title:"任务详情",dataIndex:"taskDetail",key:"taskDetail",width:"30%",align:"center"},{title:"提交时间",dataIndex:"submitTime",key:"submitTime",align:"center"},{title:"操作",key:"action",align:"center"}]),C=(0,r.KR)([]),A=()=>{const e=window.innerHeight,a=e-35-10;t.value=e<800?.63*a+"px":.7*a+"px"},I=()=>{C.value.splice(0,C.value.length),sessionStorage.getItem("currentIndex")&&(b.current=sessionStorage.getItem("currentIndex")),(0,d.XD)(b.current,b.pageSize).then((e=>{if(200===e.data.code){b.total=e.data.data.total,C.value=[];const t=e.data.data.data.filter((e=>0===e.operation));C.value.push(...t.map(((e,t)=>({key:e.id,index:t+1,taskTag:e.type,taskDetail:e.detail,submitTime:e.presentDate,operation:e.operation,evaluatedName:e.evaluatedName,department:e.department}))))}else s.Ay.error("获取失败！"+e.data.msg)})).catch((e=>{s.Ay.error("请求失败，请稍后重试！")}))},T=e=>"添加评估人审批"===e?"cyan":"添加被评估人审批"===e?"orange":"周边评议"===e?"purple":"green",x=e=>{s.Ay.success("同意成功！")},L=e=>{b.current=Number(e.current),b.pageSize=e.pageSize,sessionStorage.setItem("currentIndex",b.current),I()},_=(0,r.KR)(!1),K=()=>{_.value=!0},S=e=>{const t=e.evaluatedName,n=e.department,o=e.key;sessionStorage.setItem("evaluatedName",t),sessionStorage.setItem("department",n),sessionStorage.setItem("todoListId",o),"0"===localStorage.getItem("userRole")?a.push({path:"/quesnaireEdit"}):a.push({path:"/quesnaireEditAd"})},R=(0,r.KR)(!1),E=()=>{R.value=!0},X=(0,r.KR)(!1),z=()=>{X.value=!0},F=(0,r.KR)([]),N=(0,r.KR)([]),D=()=>{(0,d._8)().then((e=>{if(200===e.data.code){N.value=[];for(let t=0;t<=e.data.data.length-1;t++)N.value.push({value:e.data.data[t].name+"("+e.data.data[t].department+")",key:e.data.data[t].userId})}else s.Ay.error("获取失败！"+e.data.msg)})).catch((e=>{s.Ay.error("请求失败！"+e)}))},j=()=>{const e=F.value.map((e=>N.value.find((t=>t.value===e)).key));0!==e.length?e.length>3?s.Ay.warn("最多只能选择三个被评估人哦𖦹𖦹 .ᐟ.ᐟ "):(0,d.q6)(e).then((e=>{200===e.data.code?(s.Ay.success("添加成功！"),F.value=[],W(),I(),X.value=!1):s.Ay.error("添加失败！"+e.data.msg)})).catch((e=>{s.Ay.error("请求失败！"+e)})):s.Ay.warn("您还未选择被评估人哦𖦹𖦹 .ᐟ.ᐟ ")},q=(0,r.KR)([{title:"序号",dataIndex:"index",key:"index",align:"center"},{title:"姓名",dataIndex:"username",key:"username",align:"center"},{title:"部门",dataIndex:"department",key:"department",align:"center"}]),P=(0,r.KR)([]),W=()=>{(0,d.AV)().then((e=>{if(200===e.data.code){P.value=[];for(let t=0;t<=e.data.data.length-1;t++)P.value.push({index:t+1,username:e.data.data[t].username,department:e.data.data[t].department})}else s.Ay.error("获取失败！"+e.data.msg)})).catch((e=>{s.Ay.error("请求失败！"+e)}))};return(e,a)=>{const r=(0,n.g2)("a-tag"),s=(0,n.g2)("a-divider"),l=(0,n.g2)("a-table"),d=(0,n.g2)("a-button"),A=(0,n.g2)("a-select"),I=(0,n.g2)("a-modal");return(0,n.uX)(),(0,n.CE)("div",i,[(0,n.Lk)("div",u,[a[2]||(a[2]=(0,n.Lk)("div",{class:"title"},[(0,n.Lk)("span",null,"我的待办")],-1)),(0,n.Lk)("div",c,[(0,n.bF)(l,{columns:w.value,"data-source":C.value,rowKey:e=>e.key,scroll:{y:t.value},pagination:b,bordered:!0,onChange:L},{bodyCell:(0,n.k6)((({column:e,record:t})=>["taskTag"===e.key?((0,n.uX)(),(0,n.CE)("span",p,[((0,n.uX)(),(0,n.Wv)(r,{key:t.taskTag,color:T(t.taskTag)},{default:(0,n.k6)((()=>[(0,n.eW)((0,o.v_)(t.taskTag),1)])),_:2},1032,["color"]))])):(0,n.Q3)("",!0),"action"===e.key?((0,n.uX)(),(0,n.CE)(n.FK,{key:1},["添加评估人审批"===t.taskTag?((0,n.uX)(),(0,n.CE)("span",g,[(0,n.Lk)("a",{style:{color:"#0014b7","text-decoration":"underline"},onClick:E},"查看详情"),(0,n.bF)(s,{type:"vertical"}),(0,n.Lk)("a",{style:{color:"#bd3124","font-weight":"bold"},onClick:K},"拒绝")])):"添加被评估人审批"===t.taskTag?((0,n.uX)(),(0,n.CE)("span",k,[(0,n.Lk)("a",{style:{color:"#3f9373","font-weight":"bold"},onClick:e=>x(t)},"同意",8,m),(0,n.bF)(s,{type:"vertical"}),(0,n.Lk)("a",{style:{color:"#bd3124","font-weight":"bold"},onClick:K},"拒绝")])):"周边评议"===t.taskTag?((0,n.uX)(),(0,n.CE)("span",v,[(0,n.Lk)("a",{style:{color:"#3f9373","font-weight":"bold"},onClick:e=>S(t)},"进入评议",8,y)])):(0,n.Q3)("",!0)],64)):(0,n.Q3)("",!0)])),_:1},8,["columns","data-source","rowKey","scroll","pagination"])])]),(0,n.Lk)("div",h,[(0,n.bF)(d,{type:"primary",onClick:z},{default:(0,n.k6)((()=>a[3]||(a[3]=[(0,n.eW)(" 我要评估 ")]))),_:1}),(0,n.Lk)("div",f,[(0,n.bF)(l,{columns:q.value,"data-source":P.value,bordered:!1,pagination:!1,scroll:{y:"60vh"}},null,8,["columns","data-source"])]),(0,n.bF)(I,{open:X.value,"onUpdate:open":a[1]||(a[1]=e=>X.value=e),title:"添加被评估人",onOk:j},{default:(0,n.k6)((()=>[a[4]||(a[4]=(0,n.Lk)("span",{style:{color:"#bd3124"}},"注：您可以自主选择1-3个要评估的人",-1)),(0,n.bF)(A,{value:F.value,"onUpdate:value":a[0]||(a[0]=e=>F.value=e),options:N.value,mode:"multiple",placeholder:"请选择",style:{width:"100%"}},null,8,["value","options"])])),_:1},8,["open"])])])}}},w=a(3578);const C=(0,w.A)(b,[["__scopeId","data-v-74ac5537"]]);var A=C}}]);
//# sourceMappingURL=939.ea879656.js.map