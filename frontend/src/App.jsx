import React, { useCallback, useEffect, useState } from 'react';
import { Anchor, AlertTriangle, CheckCircle2, ClipboardCheck, Database, ExternalLink, FileCheck2, RefreshCcw, ShieldCheck, Ship, Waves } from 'lucide-react';
const request=async (url,opts={})=> { const response=await fetch(url,opts); if(!response.ok) throw new Error(`Request failed (${response.status}). Check that Spring Boot is running.`); return response.json(); };
const formatDate = value => value ? new Date(`${value}T12:00:00`).toLocaleDateString(undefined,{month:'short',day:'numeric',year:'numeric'}) : '—';
const classFor = p => p==='HIGH' ? 'high':p==='MEDIUM'?'medium':'low';
export default function App() {
 const [fleet,setFleet]=useState([]),[selected,setSelected]=useState(1),[readiness,setReadiness]=useState(null),[actions,setActions]=useState([]),[loading,setLoading]=useState(true),[saving,setSaving]=useState(null),[error,setError]=useState('');
 const refresh=useCallback(async ()=>{
  try {setError('');const [v,r,a]=await Promise.all([request('/api/vessels'),request(`/api/vessels/${selected}/readiness`),request(`/api/vessels/${selected}/recommendations`)]);setFleet(v);setReadiness(r);setActions(a.actions);}
  catch(e){setError(e.message);}finally{setLoading(false);}
 },[selected]);
 useEffect(()=>{setLoading(true);refresh();},[refresh]);
 async function toggleFinding(f) {setSaving(f.id);try{await request(`/api/findings/${f.id}`,{method:'PATCH',headers:{'Content-Type':'application/json'},body:JSON.stringify({status:f.status==='OPEN'?'RESOLVED':'OPEN'})});await refresh();}catch(e){setError(e.message);}finally{setSaving(null);}}
 const high=fleet.filter(v=>v.priority==='HIGH').length, open=fleet.reduce((n,v)=>n+v.openFindings,0);
 return <div className="app-shell">
  <aside className="sidebar"><div className="brand"><div className="brand-icon"><Anchor size={24}/></div><div><strong>PORTREADY<span> AI</span></strong><small>MARITIME OPERATIONS · DEMO</small></div></div>
   <div className="side-label">WORKSPACE</div><div className="side-item active"><Waves size={17}/> Fleet overview</div><div className="side-item"><ShieldCheck size={17}/> Readiness workflow</div>
   <div className="side-label second">SELECT A VESSEL</div><div className="vessel-links">{fleet.map(v=><button key={v.id} onClick={()=>setSelected(v.id)} className={'vessel-link '+(selected===v.id?'selected':'')}><Ship size={16}/><span>{v.name}</span><i className={'dot '+classFor(v.priority)}/></button>)}</div>
   
  </aside>
  <main className="main">
   <header className="topbar"><div className="eyebrow">FLEET OPERATIONS / READINESS</div><div className="demo-pill"><span className="pulse"/> LOCAL DEMONSTRATION</div></header>
   <div className="content"><div className="heading"><div><div className="overline">OPERATIONS DASHBOARD</div><h1>Vessel readiness overview</h1><p>Prioritize document reviews and outstanding inspection actions before arrival.</p></div><button className="refresh" onClick={()=>{setLoading(true);refresh();}}><RefreshCcw size={16}/> Refresh</button></div>
    
    {error && <div role="alert" className="error">{error}</div>}
    <div className="stats"><div className="stat"><span><Ship size={17}/> VESSELS IN FLEET</span><strong>{fleet.length}</strong><small>Fictional sample fleet</small></div><div className="stat"><span><AlertTriangle size={17}/> HIGH PRIORITY</span><strong className="red">{high}</strong><small>Requiring review attention</small></div><div className="stat"><span><ClipboardCheck size={17}/> OPEN FINDINGS</span><strong>{open}</strong><small>Across all demo vessels</small></div></div>
    <div className="section-head"><div><h2>Fleet review queue</h2><p>Choose a vessel to inspect its readiness details.</p></div><span className="label">{fleet.length} RECORDS</span></div>
    <div className="table-wrap"><table><thead><tr><th>VESSEL</th><th>DESTINATION</th><th>ARRIVAL</th><th>OPEN ITEMS</th><th>REVIEW PRIORITY</th></tr></thead><tbody>{fleet.map(v=><tr key={v.id} onClick={()=>setSelected(v.id)} className={selected===v.id?'row-selected':''}><td><strong>{v.name}</strong><small>{v.imoNumber}</small></td><td>{v.destinationPort}</td><td>{formatDate(v.arrivalDate)}</td><td>{v.openFindings}</td><td><span className={'priority '+classFor(v.priority)}>{v.priority} <span>{v.score}/100</span></span></td></tr>)}</tbody></table>{loading&&<div className="loading">Loading fleet data…</div>}</div>
    {readiness && <div className="detail"><div className="section-head detail-heading"><div><div className="overline">VESSEL DETAIL</div><h2>{readiness.vesselName}</h2><p>Arrival in {readiness.daysToArrival} day(s) · Priority is recalculated after any status change.</p></div><span className={'priority large '+classFor(readiness.priority)}>{readiness.priority} · {readiness.score}/100</span></div>
     <div className="detail-grid"><section className="panel"><div className="panel-title"><FileCheck2 size={18}/> Certificate review</div>{readiness.certificates.length===0?<p>No records</p>:readiness.certificates.map(c=><div className="cert" key={c.id}><div><strong>{c.certificateType}</strong><small>Expiration: {formatDate(c.expiryDate)}</small></div><span className="outline-tag">CHECK DATE</span></div>)}</section>
     <section className="panel"><div className="panel-title"><ClipboardCheck size={18}/> Inspection findings</div>{readiness.findings.length===0?<p>No outstanding records</p>:readiness.findings.map(f=><div className="finding" key={f.id}><div><strong>{f.description}</strong><small>{f.status==='OPEN'?'Unresolved':'Marked resolved'}</small></div><button disabled={saving===f.id} onClick={()=>toggleFinding(f)} className={f.status==='OPEN'?'resolve':'reopen'}>{f.status==='OPEN'?<><CheckCircle2 size={14}/> Resolve</>:'Reopen'}</button></div>)}</section></div>
     <div className="detail-grid bottom"><section className="panel"><div className="panel-title"><AlertTriangle size={18}/> Why this priority?</div><ul className="reasons">{readiness.reasons.map((r,i)=><li key={i}>{r}</li>)}</ul><p className="tiny">Points are invented to illustrate explainable workflow sorting, not probability of detention.</p></section><section className="panel"><div className="panel-title"><ShieldCheck size={18}/> Recommended next steps</div><ol className="actions">{actions.map((a,i)=><li key={i}>{a}</li>)}</ol></section></div>
    </div>}
    <footer><div><strong>PortReady AI</strong> · independent portfolio prototype</div><div><ExternalLink size={13}/> Java + Spring Boot + JDBC + React</div></footer>
   </div>
  </main>
 </div>;
}
