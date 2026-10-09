import{n as ui,i as _,r as oo,a as hi,T as qt,t as pi,w as fi,e as mi,b as _i,_ as E}from"./indexhtml-CHKSUbna.js";const{toString:gi}=Object.prototype;function vi(o){return gi.call(o)==="[object RegExp]"}function bi(o,{preserve:e=!0,whitespace:t=!0,all:i}={}){if(i)throw new Error("The `all` option is no longer supported. Use the `preserve` option instead.");let r=e,s;typeof e=="function"?(r=!1,s=e):vi(e)&&(r=!1,s=c=>e.test(c));let n=!1,a="",l="",d="";for(let c=0;c<o.length;c++){if(a=o[c],o[c-1]!=="\\"&&(a==='"'||a==="'")&&(n===a?n=!1:n||(n=a)),!n&&a==="/"&&o[c+1]==="*"){const u=o[c+2]==="!";let h=c+2;for(;h<o.length;h++){if(o[h]==="*"&&o[h+1]==="/"){r&&u||s&&s(l)?d+=`/*${l}*/`:t||(o[h+2]===`
`?h++:o[h+2]+o[h+3]===`\r
`&&(h+=2)),l="";break}l+=o[h]}c=h+1;continue}d+=a}return d}const yi=CSSStyleSheet.toString().includes("document.createElement"),xi=(o,e)=>{const t=/(?:@media\s(.+?))?(?:\s{)?\@import\s*(?:url\(\s*['"]?(.+?)['"]?\s*\)|(["'])((?:\\.|[^\\])*?)\3)([^;]*);(?:})?/g;/\/\*(.|[\r\n])*?\*\//gm.exec(o)!=null&&(o=bi(o));for(var i,r=o;(i=t.exec(o))!==null;){r=r.replace(i[0],"");const s=document.createElement("link");s.rel="stylesheet",s.href=i[2]||i[4];const n=i[1]||i[5];n&&(s.media=n),e===document?document.head.appendChild(s):e.appendChild(s)}return r},wi=(o,e,t)=>(e.adoptedStyleSheets=[...e.adoptedStyleSheets,o],()=>{e.adoptedStyleSheets=e.adoptedStyleSheets.filter(i=>i!==o)}),Ci=(o,e,t)=>{const i=new CSSStyleSheet;return i.replaceSync(o),yi?wi(i,e):(e.adoptedStyleSheets.push(i),()=>{e.adoptedStyleSheets.splice(e.adoptedStyleSheets.indexOf(i),1)})},Ai=(o,e)=>{const t=document.createElement("style");t.type="text/css",t.textContent=o;let i;{const s=Array.from(document.head.childNodes).filter(n=>n.nodeType===Node.COMMENT_NODE).find(n=>n.data.trim()===e);s&&(i=s)}return document.head.insertBefore(t,i),()=>{t.remove()}},Ei=(o,e,t,i)=>{if(t===document){const s=Pi(o);if(window.Vaadin.theme.injectedGlobalCss.indexOf(s)!==-1)return;window.Vaadin.theme.injectedGlobalCss.push(s)}const r=xi(o,t);return t===document?Ai(r,e):Ci(r,t)};window.Vaadin=window.Vaadin||{};window.Vaadin.theme=window.Vaadin.theme||{};window.Vaadin.theme.injectedGlobalCss=[];function Ce(o){let e,t,i=2166136261;for(e=0,t=o.length;e<t;e++)i^=o.charCodeAt(e),i+=(i<<1)+(i<<4)+(i<<7)+(i<<8)+(i<<24);return("0000000"+(i>>>0).toString(16)).substr(-8)}function Pi(o){let e=Ce(o);return e+Ce(e+o)}/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const io=o=>class extends o{static get properties(){return{_theme:{type:String,readOnly:!0}}}static get observedAttributes(){return[...super.observedAttributes,"theme"]}attributeChangedCallback(t,i,r){super.attributeChangedCallback(t,i,r),t==="theme"&&this._set_theme(r)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const B=[];function ro(o){return o&&Object.prototype.hasOwnProperty.call(o,"__themes")}function Ti(o){return ro(customElements.get(o))}function Si(o=[]){return[o].flat(1/0).filter(e=>e instanceof ui?!0:(console.warn("An item in styles is not of type CSSResult. Use `unsafeCSS` or `css`."),!1))}function g(o,e,t={}){o&&Ti(o)&&console.warn(`The custom element definition for "${o}"
      was finalized before a style module was registered.
      Make sure to add component specific style modules before
      importing the corresponding custom element.`),e=Si(e),window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.registerStyles(o,e,t):B.push({themeFor:o,styles:e,include:t.include,moduleId:t.moduleId})}function Wt(){return window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.getAllThemes():B}function ki(o,e){return(o||"").split(" ").some(t=>new RegExp(`^${t.split("*").join(".*")}$`,"u").test(e))}function Oi(o=""){let e=0;return o.startsWith("lumo-")||o.startsWith("material-")?e=1:o.startsWith("vaadin-")&&(e=2),e}function so(o){const e=[];return o.include&&[].concat(o.include).forEach(t=>{const i=Wt().find(r=>r.moduleId===t);i?e.push(...so(i),...i.styles):console.warn(`Included moduleId ${t} not found in style registry`)},o.styles),e}function zi(o,e){const t=document.createElement("style");t.innerHTML=o.map(i=>i.cssText).join(`
`),e.content.appendChild(t)}function Mi(o){const e=`${o}-default-theme`,t=Wt().filter(i=>i.moduleId!==e&&ki(i.themeFor,o)).map(i=>({...i,styles:[...so(i),...i.styles],includePriority:Oi(i.moduleId)})).sort((i,r)=>r.includePriority-i.includePriority);return t.length>0?t:Wt().filter(i=>i.moduleId===e)}const nt=o=>class extends io(o){static finalize(){if(super.finalize(),this.elementStyles)return;const t=this.prototype._template;!t||ro(this)||zi(this.getStylesForThis(),t)}static finalizeStyles(t){const i=this.getStylesForThis();return t?[...super.finalizeStyles(t),...i]:i}static getStylesForThis(){const t=Object.getPrototypeOf(this.prototype),i=(t?t.constructor.__themes:[])||[];this.__themes=[...i,...Mi(this.is)];const r=this.__themes.flatMap(s=>s.styles);return r.filter((s,n)=>n===r.lastIndexOf(s))}},Ni=_`:root{--st-bg-main: #F6F8FB;--st-bg-surface: #FFFFFF;--st-bg-surface-secondary: #F8FAFC;--st-text-primary: #172B4D;--st-text-secondary: #5E6C84;--st-text-muted: #7A869A;--st-border: #DFE5EE;--st-hover-bg: #F1F5F9;--st-blue: #2563EB;--st-blue-hover: #1D4ED8;--st-blue-pressed: #1E40AF;--st-green: #15803D;--st-green-bg: #DCFCE7;--st-red: #DC2626;--st-red-bg: #FEE2E2;--st-focus: #93C5FD;--st-radius: 7px;--st-radius-lg: 9px;--st-font: "Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;--st-transition: .15s ease}@media (prefers-reduced-motion: reduce){:root{--st-transition: 0ms}}html{--lumo-font-family: var(--st-font);--lumo-primary-color: var(--st-blue);--lumo-primary-text-color: var(--st-blue);--lumo-success-color: var(--st-green);--lumo-success-text-color: var(--st-green);--lumo-error-color: var(--st-red);--lumo-error-text-color: var(--st-red);--lumo-body-text-color: var(--st-text-primary);--lumo-secondary-text-color: var(--st-text-secondary);--lumo-contrast-10pct: var(--st-border);--lumo-border-radius-m: var(--st-radius);--lumo-size-m: 42px;--lumo-size-l: 44px}vaadin-app-layout{--vaadin-app-layout-drawer-offset-size: 224px}vaadin-app-layout::part(navbar){height:64px;min-height:64px;border-bottom:1px solid var(--st-border);box-shadow:none;background:var(--st-bg-surface)}vaadin-app-layout::part(drawer){width:224px;background:var(--st-bg-surface);border-right:1px solid var(--st-border);padding-top:0}vaadin-app-layout [slot=drawer]{padding:0}.st-drawer-brand{display:flex;align-items:center;gap:12px;padding:16px 24px;border-bottom:1px solid var(--st-border);min-height:64px;box-sizing:border-box}.st-drawer-brand svg{color:var(--st-blue);width:22px;height:22px}.st-drawer-brand .st-brand-text{font-size:18px;font-weight:600;color:var(--st-text-primary);letter-spacing:-.2px}.st-nav-link{display:flex;align-items:center;gap:12px;padding:0 20px;height:48px;text-decoration:none!important;color:var(--st-text-secondary);font-size:14px;font-weight:500;border-left:3px solid transparent;transition:background-color var(--st-transition),color var(--st-transition),border-color var(--st-transition);cursor:pointer}.st-nav-link:hover{background-color:var(--st-hover-bg);color:var(--st-text-primary)}.st-nav-link:active{background-color:var(--st-border)}.st-nav-link:focus-visible{outline:2px solid var(--st-focus);outline-offset:-2px}.st-nav-link.active{color:var(--st-blue);background-color:#2563eb0d;border-left-color:var(--st-blue);font-weight:600}.st-nav-link svg{width:18px;height:18px;flex-shrink:0;color:inherit}.st-navbar{display:flex;align-items:center;width:100%;height:100%;padding:0 24px 0 0;gap:12px}.st-navbar-user{margin-left:auto;display:flex;align-items:center;gap:12px}.st-navbar-username{font-size:14px;color:var(--st-text-primary);font-weight:500}.st-avatar{width:32px;height:32px;border-radius:50%;background:var(--st-blue);color:#fff;display:flex;align-items:center;justify-content:center;font-weight:600;font-size:14px}.st-navbar-logout{font-size:13px;color:var(--st-text-secondary);cursor:pointer;background:var(--st-bg-surface);border:1px solid var(--st-border);font-weight:500;padding:6px 12px;border-radius:6px;transition:background var(--st-transition),color var(--st-transition),border-color var(--st-transition);display:flex;align-items:center;gap:6px}.st-navbar-logout:hover{background:var(--st-hover-bg);color:var(--st-text-primary);border-color:#cbd5e1}.st-navbar-logout svg{width:14px;height:14px}.st-page{background:var(--st-bg-main);padding:24px;min-height:100%}@media (max-width: 768px){.st-page{padding:16px}}.st-header-container{display:flex;flex-wrap:wrap;justify-content:space-between;align-items:flex-start;gap:16px;margin-bottom:20px}.st-page-title-group{display:flex;flex-direction:column}.st-page-header{margin-bottom:20px}.st-page-title{font-size:26px;font-weight:600;color:var(--st-text-primary);margin:0;line-height:1.3}.st-page-subtitle{font-size:14px;color:var(--st-text-secondary);margin:4px 0 0}.st-stats-row{display:flex;gap:16px;flex-wrap:wrap}.st-stat-card{background:var(--st-bg-surface);border:1px solid var(--st-border);border-radius:var(--st-radius-lg);padding:16px;width:200px;flex:0 0 auto;display:flex;flex-direction:column;justify-content:center}.st-stat-label{font-size:13px;color:var(--st-text-secondary);font-weight:500;margin-bottom:6px}.st-stat-value{font-size:22px;font-weight:600;color:var(--st-text-primary)}.st-section-title{font-size:18px;font-weight:600;color:var(--st-text-primary);margin:0 0 8px}.st-section-subtitle{font-size:14px;color:var(--st-text-secondary);margin:0 0 16px}.st-table-surface{background:var(--st-bg-surface);border:1px solid var(--st-border);border-radius:var(--st-radius-lg);overflow:hidden}vaadin-grid{--lumo-base-color: var(--st-bg-surface);font-size:14px;border:none!important}vaadin-grid::part(header-cell){font-size:13px;font-weight:500;color:var(--st-text-secondary);background:var(--st-bg-surface);border-bottom:1px solid var(--st-border);padding:12px 16px}vaadin-grid::part(body-cell){font-size:14px;color:var(--st-text-primary);min-height:52px;border-bottom:1px solid #f1f5f9;padding:0 16px}vaadin-grid::part(body-cell)::-webkit-scrollbar{display:none}vaadin-grid::part(row):hover{background:var(--st-hover-bg)}.st-btn{border:none;border-radius:var(--st-radius);font-size:14px;font-weight:500;cursor:pointer;padding:0 16px;height:40px;line-height:40px;transition:background var(--st-transition),color var(--st-transition),border-color var(--st-transition),opacity var(--st-transition);display:inline-flex;align-items:center;justify-content:center;gap:8px;box-sizing:border-box;white-space:nowrap;flex-shrink:0}.st-btn:disabled{opacity:.5;cursor:not-allowed}.st-btn:focus-visible{outline:2px solid var(--st-focus);outline-offset:2px}.st-btn-primary{background:var(--st-blue);color:#fff}.st-btn-primary:hover:not(:disabled){background:var(--st-blue-hover)}.st-btn-primary:active:not(:disabled){background:var(--st-blue-pressed)}.st-btn-buy{background:var(--st-green);color:#fff}.st-btn-buy:hover:not(:disabled){background:#166534}.st-btn-buy:active:not(:disabled){background:#14532d}.st-btn-sell{background:var(--st-red);color:#fff}.st-btn-sell:hover:not(:disabled){background:#b91c1c}.st-btn-sell:active:not(:disabled){background:#991b1b}.st-btn-secondary{background:var(--st-bg-surface);color:var(--st-text-primary);border:1px solid var(--st-border)}.st-btn-secondary:hover:not(:disabled){background:var(--st-hover-bg)}.st-btn-secondary:active:not(:disabled){background:#e2e8f0}.st-btn-text{background:transparent;color:var(--st-text-secondary);height:32px;line-height:32px;padding:0 8px}.st-btn-text:hover:not(:disabled){color:var(--st-text-primary);background:var(--st-hover-bg)}.st-badge{display:inline-flex;align-items:center;font-size:12px;font-weight:600;padding:4px 8px;border-radius:4px}.st-badge-buy{background:var(--st-green-bg);color:var(--st-green)}.st-badge-sell{background:var(--st-red-bg);color:var(--st-red)}.st-badge-sector{color:var(--st-text-secondary);font-size:13px;font-weight:400}.st-positive{color:var(--st-green)!important;font-weight:500}.st-negative{color:var(--st-red)!important;font-weight:500}.st-empty-state{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:64px 24px;text-align:center}.st-empty-state svg{width:48px;height:48px;color:var(--st-text-muted);margin-bottom:16px}.st-empty-title{font-size:18px;font-weight:600;color:var(--st-text-primary);margin:0 0 8px}.st-empty-text{font-size:14px;color:var(--st-text-secondary);margin:0 0 24px;max-width:400px;line-height:1.5}.st-search-field{width:320px;max-width:100%}.st-search-field::part(input-field){background:var(--st-bg-surface);border:1px solid var(--st-border);border-radius:var(--st-radius);height:40px}.st-search-field::part(input-field):focus-within{border-color:var(--st-focus);box-shadow:0 0 0 2px #93c5fd4d}vaadin-dialog-overlay::part(overlay){border-radius:var(--st-radius-lg);background:var(--st-bg-surface)}.st-dialog-row{display:flex;justify-content:space-between;align-items:center;padding:10px 0;width:100%;box-sizing:border-box}.st-dialog-row-label{font-size:14px;color:var(--st-text-secondary);font-weight:500}.st-dialog-row-value{font-size:14px;font-weight:600;color:var(--st-text-primary)}.st-dialog-estimate{background:var(--st-bg-surface-secondary);border:1px solid var(--st-border);border-radius:var(--st-radius);padding:16px;margin-top:20px;width:100%;box-sizing:border-box}.st-dialog-estimate .st-dialog-row{padding:0}.st-dialog-note{font-size:12px;color:var(--st-text-muted);margin-top:8px}.st-login-page{display:flex;min-height:100vh;background:var(--st-bg-main)}.st-login-brand-panel{flex:1;background:var(--st-bg-surface-secondary);display:flex;flex-direction:column;justify-content:center;padding:64px 80px;position:relative;border-right:1px solid var(--st-border)}.st-login-brand-logo{display:flex;align-items:center;gap:12px;margin-bottom:32px}.st-login-brand-logo svg{width:32px;height:32px;color:var(--st-blue)}.st-login-brand-name{font-size:24px;font-weight:700;color:var(--st-text-primary);letter-spacing:-.3px}.st-login-brand-heading{font-size:32px;font-weight:600;color:var(--st-text-primary);line-height:1.3;margin:0 0 16px;max-width:400px}.st-login-brand-text{font-size:16px;color:var(--st-text-secondary);line-height:1.6;max-width:400px}.st-login-form-panel{flex:1;display:flex;align-items:center;justify-content:center;padding:48px;background:var(--st-bg-main)}.st-login-form-card{width:100%;max-width:400px;background:var(--st-bg-surface);border:1px solid var(--st-border);border-radius:var(--st-radius-lg);padding:32px}.st-login-form-title{font-size:22px;font-weight:600;color:var(--st-text-primary);margin:0 0 8px}.st-login-form-subtitle{font-size:14px;color:var(--st-text-secondary);margin:0 0 32px}.st-login-footer{text-align:center;font-size:13px;color:var(--st-text-secondary);margin-top:24px}@media (max-width: 900px){.st-login-brand-panel{display:none}.st-login-form-panel{padding:24px}}.st-symbol{font-weight:600;color:var(--st-text-primary);font-size:14px}`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function P(o){const e=customElements.get(o.is);if(!e)Object.defineProperty(o,"version",{get(){return"24.3.0"}}),customElements.define(o.is,o);else{const t=e.version;t&&o.version&&t===o.version?console.warn(`The component ${o.is} has been loaded twice`):console.error(`Tried to define ${o.is} version ${o.version} when version ${e.version} is already in use. Something will probably break.`)}}/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Ii extends HTMLElement{static get is(){return"vaadin-lumo-styles"}}P(Ii);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Li=(o,...e)=>{const t=document.createElement("style");t.id=o,t.textContent=e.map(i=>i.toString()).join(`
`).replace(":host","html"),document.head.insertAdjacentElement("afterbegin",t)},O=(o,...e)=>{Li(`lumo-${o}`,e)};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Di=_`
  :host {
    /* Base (background) */
    --lumo-base-color: #fff;

    /* Tint */
    --lumo-tint-5pct: hsla(0, 0%, 100%, 0.3);
    --lumo-tint-10pct: hsla(0, 0%, 100%, 0.37);
    --lumo-tint-20pct: hsla(0, 0%, 100%, 0.44);
    --lumo-tint-30pct: hsla(0, 0%, 100%, 0.5);
    --lumo-tint-40pct: hsla(0, 0%, 100%, 0.57);
    --lumo-tint-50pct: hsla(0, 0%, 100%, 0.64);
    --lumo-tint-60pct: hsla(0, 0%, 100%, 0.7);
    --lumo-tint-70pct: hsla(0, 0%, 100%, 0.77);
    --lumo-tint-80pct: hsla(0, 0%, 100%, 0.84);
    --lumo-tint-90pct: hsla(0, 0%, 100%, 0.9);
    --lumo-tint: #fff;

    /* Shade */
    --lumo-shade-5pct: hsla(214, 61%, 25%, 0.05);
    --lumo-shade-10pct: hsla(214, 57%, 24%, 0.1);
    --lumo-shade-20pct: hsla(214, 53%, 23%, 0.16);
    --lumo-shade-30pct: hsla(214, 50%, 22%, 0.26);
    --lumo-shade-40pct: hsla(214, 47%, 21%, 0.38);
    --lumo-shade-50pct: hsla(214, 45%, 20%, 0.52);
    --lumo-shade-60pct: hsla(214, 43%, 19%, 0.6);
    --lumo-shade-70pct: hsla(214, 42%, 18%, 0.69);
    --lumo-shade-80pct: hsla(214, 41%, 17%, 0.83);
    --lumo-shade-90pct: hsla(214, 40%, 16%, 0.94);
    --lumo-shade: hsl(214, 35%, 15%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-shade-5pct);
    --lumo-contrast-10pct: var(--lumo-shade-10pct);
    --lumo-contrast-20pct: var(--lumo-shade-20pct);
    --lumo-contrast-30pct: var(--lumo-shade-30pct);
    --lumo-contrast-40pct: var(--lumo-shade-40pct);
    --lumo-contrast-50pct: var(--lumo-shade-50pct);
    --lumo-contrast-60pct: var(--lumo-shade-60pct);
    --lumo-contrast-70pct: var(--lumo-shade-70pct);
    --lumo-contrast-80pct: var(--lumo-shade-80pct);
    --lumo-contrast-90pct: var(--lumo-shade-90pct);
    --lumo-contrast: var(--lumo-shade);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 100%, 48%);
    --lumo-primary-color-50pct: hsla(214, 100%, 49%, 0.76);
    --lumo-primary-color-10pct: hsla(214, 100%, 60%, 0.13);
    --lumo-primary-text-color: hsl(214, 100%, 43%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 85%, 48%);
    --lumo-error-color-50pct: hsla(3, 85%, 49%, 0.5);
    --lumo-error-color-10pct: hsla(3, 85%, 49%, 0.1);
    --lumo-error-text-color: hsl(3, 89%, 42%);
    --lumo-error-contrast-color: #fff;

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 72%, 31%, 0.5);
    --lumo-success-color-10pct: hsla(145, 72%, 31%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 25%);
    --lumo-success-contrast-color: #fff;

    /* Warning */
    --lumo-warning-color: hsl(48, 100%, 50%);
    --lumo-warning-color-10pct: hsla(48, 100%, 50%, 0.25);
    --lumo-warning-text-color: hsl(32, 100%, 30%);
    --lumo-warning-contrast-color: var(--lumo-shade-90pct);
  }

  /* forced-colors mode adjustments */
  @media (forced-colors: active) {
    html {
      --lumo-disabled-text-color: GrayText;
    }
  }
`;O("color-props",Di);const no=_`
  [theme~='dark'] {
    /* Base (background) */
    --lumo-base-color: hsl(214, 35%, 21%);

    /* Tint */
    --lumo-tint-5pct: hsla(214, 65%, 85%, 0.06);
    --lumo-tint-10pct: hsla(214, 60%, 80%, 0.14);
    --lumo-tint-20pct: hsla(214, 64%, 82%, 0.23);
    --lumo-tint-30pct: hsla(214, 69%, 84%, 0.32);
    --lumo-tint-40pct: hsla(214, 73%, 86%, 0.41);
    --lumo-tint-50pct: hsla(214, 78%, 88%, 0.5);
    --lumo-tint-60pct: hsla(214, 82%, 90%, 0.58);
    --lumo-tint-70pct: hsla(214, 87%, 92%, 0.69);
    --lumo-tint-80pct: hsla(214, 91%, 94%, 0.8);
    --lumo-tint-90pct: hsla(214, 96%, 96%, 0.9);
    --lumo-tint: hsl(214, 100%, 98%);

    /* Shade */
    --lumo-shade-5pct: hsla(214, 0%, 0%, 0.07);
    --lumo-shade-10pct: hsla(214, 4%, 2%, 0.15);
    --lumo-shade-20pct: hsla(214, 8%, 4%, 0.23);
    --lumo-shade-30pct: hsla(214, 12%, 6%, 0.32);
    --lumo-shade-40pct: hsla(214, 16%, 8%, 0.41);
    --lumo-shade-50pct: hsla(214, 20%, 10%, 0.5);
    --lumo-shade-60pct: hsla(214, 24%, 12%, 0.6);
    --lumo-shade-70pct: hsla(214, 28%, 13%, 0.7);
    --lumo-shade-80pct: hsla(214, 32%, 13%, 0.8);
    --lumo-shade-90pct: hsla(214, 33%, 13%, 0.9);
    --lumo-shade: hsl(214, 33%, 13%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-tint-5pct);
    --lumo-contrast-10pct: var(--lumo-tint-10pct);
    --lumo-contrast-20pct: var(--lumo-tint-20pct);
    --lumo-contrast-30pct: var(--lumo-tint-30pct);
    --lumo-contrast-40pct: var(--lumo-tint-40pct);
    --lumo-contrast-50pct: var(--lumo-tint-50pct);
    --lumo-contrast-60pct: var(--lumo-tint-60pct);
    --lumo-contrast-70pct: var(--lumo-tint-70pct);
    --lumo-contrast-80pct: var(--lumo-tint-80pct);
    --lumo-contrast-90pct: var(--lumo-tint-90pct);
    --lumo-contrast: var(--lumo-tint);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 90%, 48%);
    --lumo-primary-color-50pct: hsla(214, 90%, 70%, 0.69);
    --lumo-primary-color-10pct: hsla(214, 90%, 55%, 0.13);
    --lumo-primary-text-color: hsl(214, 90%, 77%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 79%, 49%);
    --lumo-error-color-50pct: hsla(3, 75%, 62%, 0.5);
    --lumo-error-color-10pct: hsla(3, 75%, 62%, 0.14);
    --lumo-error-text-color: hsl(3, 100%, 80%);

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 92%, 51%, 0.5);
    --lumo-success-color-10pct: hsla(145, 92%, 51%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 46%);

    /* Warning */
    --lumo-warning-color: hsl(43, 100%, 48%);
    --lumo-warning-color-10pct: hsla(40, 100%, 50%, 0.2);
    --lumo-warning-text-color: hsl(45, 100%, 60%);
    --lumo-warning-contrast-color: var(--lumo-shade-90pct);
  }

  html {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: light;
  }

  [theme~='dark'] {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: dark;
  }

  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    color: var(--lumo-header-text-color);
  }

  a:where(:any-link) {
    color: var(--lumo-primary-text-color);
  }

  a:not(:any-link) {
    color: var(--lumo-disabled-text-color);
  }

  blockquote {
    color: var(--lumo-secondary-text-color);
  }

  code,
  pre {
    background-color: var(--lumo-contrast-10pct);
    border-radius: var(--lumo-border-radius-m);
  }
`;g("",no,{moduleId:"lumo-color"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Fi=_`
  :host {
    /* Border radius */
    --lumo-border-radius-s: 0.25em; /* Checkbox, badge, date-picker year indicator, etc */
    --lumo-border-radius-m: var(--lumo-border-radius, 0.25em); /* Button, text field, menu overlay, etc */
    --lumo-border-radius-l: 0.5em; /* Dialog, notification, etc */

    /* Shadow */
    --lumo-box-shadow-xs: 0 1px 4px -1px var(--lumo-shade-50pct);
    --lumo-box-shadow-s: 0 2px 4px -1px var(--lumo-shade-20pct), 0 3px 12px -1px var(--lumo-shade-30pct);
    --lumo-box-shadow-m: 0 2px 6px -1px var(--lumo-shade-20pct), 0 8px 24px -4px var(--lumo-shade-40pct);
    --lumo-box-shadow-l: 0 3px 18px -2px var(--lumo-shade-20pct), 0 12px 48px -6px var(--lumo-shade-40pct);
    --lumo-box-shadow-xl: 0 4px 24px -3px var(--lumo-shade-20pct), 0 18px 64px -8px var(--lumo-shade-40pct);

    /* Clickable element cursor */
    --lumo-clickable-cursor: default;
  }
`;_`
  html {
    /* Button */
    --vaadin-button-background: var(--lumo-contrast-5pct);
    --vaadin-button-border: none;
    --vaadin-button-border-radius: var(--lumo-border-radius-m);
    --vaadin-button-font-size: var(--lumo-font-size-m);
    --vaadin-button-font-weight: 500;
    --vaadin-button-height: var(--lumo-size-m);
    --vaadin-button-margin: var(--lumo-space-xs) 0;
    --vaadin-button-min-width: calc(var(--vaadin-button-height) * 2);
    --vaadin-button-padding: 0 calc(var(--vaadin-button-height) / 3 + var(--lumo-border-radius-m) / 2);
    --vaadin-button-text-color: var(--lumo-primary-text-color);
    --vaadin-button-primary-background: var(--lumo-primary-color);
    --vaadin-button-primary-border: none;
    --vaadin-button-primary-font-weight: 600;
    --vaadin-button-primary-text-color: var(--lumo-primary-contrast-color);
    --vaadin-button-tertiary-background: transparent !important;
    --vaadin-button-tertiary-text-color: var(--lumo-primary-text-color);
    --vaadin-button-tertiary-font-weight: 500;
    --vaadin-button-tertiary-padding: 0 calc(var(--vaadin-button-height) / 6);
    /* Checkbox */
    --vaadin-checkbox-background: var(--lumo-contrast-20pct);
    --vaadin-checkbox-background-hover: var(--lumo-contrast-30pct);
    --vaadin-checkbox-border-radius: var(--lumo-border-radius-s);
    --vaadin-checkbox-checkmark-char: var(--lumo-icons-checkmark);
    --vaadin-checkbox-checkmark-char-indeterminate: '';
    --vaadin-checkbox-checkmark-color: var(--lumo-primary-contrast-color);
    --vaadin-checkbox-checkmark-size: calc(var(--vaadin-checkbox-size) + 2px);
    --vaadin-checkbox-label-color: var(--lumo-body-text-color);
    --vaadin-checkbox-label-font-size: var(--lumo-font-size-m);
    --vaadin-checkbox-label-padding: var(--lumo-space-xs) var(--lumo-space-s) var(--lumo-space-xs) var(--lumo-space-xs);
    --vaadin-checkbox-size: calc(var(--lumo-size-m) / 2);
    /* Radio button */
    --vaadin-radio-button-background: var(--lumo-contrast-20pct);
    --vaadin-radio-button-background-hover: var(--lumo-contrast-30pct);
    --vaadin-radio-button-dot-color: var(--lumo-primary-contrast-color);
    --vaadin-radio-button-dot-size: 3px;
    --vaadin-radio-button-label-color: var(--lumo-body-text-color);
    --vaadin-radio-button-label-font-size: var(--lumo-font-size-m);
    --vaadin-radio-button-label-padding: var(--lumo-space-xs) var(--lumo-space-s) var(--lumo-space-xs)
      var(--lumo-space-xs);
    --vaadin-radio-button-size: calc(var(--lumo-size-m) / 2);
    --vaadin-selection-color: var(--lumo-primary-color);
    --vaadin-selection-color-text: var(--lumo-primary-text-color);
    --vaadin-input-field-border-radius: var(--lumo-border-radius-m);
    --vaadin-focus-ring-color: var(--lumo-primary-color-50pct);
    --vaadin-focus-ring-width: 2px;
    /* Label */
    --vaadin-input-field-label-color: var(--lumo-secondary-text-color);
    --vaadin-input-field-focused-label-color: var(--lumo-primary-text-color);
    --vaadin-input-field-hovered-label-color: var(--lumo-body-text-color);
    --vaadin-input-field-label-font-size: var(--lumo-font-size-s);
    --vaadin-input-field-label-font-weight: 500;
    /* Helper */
    --vaadin-input-field-helper-color: var(--lumo-secondary-text-color);
    --vaadin-input-field-helper-font-size: var(--lumo-font-size-xs);
    --vaadin-input-field-helper-font-weight: 400;
    --vaadin-input-field-helper-spacing: 0.4em;
    /* Error message */
    --vaadin-input-field-error-color: var(--lumo-error-text-color);
    --vaadin-input-field-error-font-size: var(--lumo-font-size-xs);
    --vaadin-input-field-error-font-weight: 400;
    /* Input field */
    --vaadin-input-field-background: var(--lumo-contrast-10pct);
    --vaadin-input-field-icon-color: var(--lumo-contrast-60pct);
    --vaadin-input-field-icon-size: var(--lumo-icon-size-m);
    --vaadin-input-field-invalid-background: var(--lumo-error-color-10pct);
    --vaadin-input-field-invalid-hover-highlight: var(--lumo-error-color-50pct);
    --vaadin-input-field-height: var(--lumo-size-m);
    --vaadin-input-field-hover-highlight: var(--lumo-contrast-50pct);
    --vaadin-input-field-placeholder-color: var(--lumo-secondary-text-color);
    --vaadin-input-field-readonly-border: 1px dashed var(--lumo-contrast-30pct);
    --vaadin-input-field-value-color: var(--lumo-body-text-color);
    --vaadin-input-field-value-font-size: var(--lumo-font-size-m);
    --vaadin-input-field-value-font-weight: 400;
  }
`;O("style-props",Fi);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ri=_`
  :host {
    /* prettier-ignore */
    --lumo-font-family: -apple-system, BlinkMacSystemFont, 'Roboto', 'Segoe UI', Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';

    /* Font sizes */
    --lumo-font-size-xxs: 0.75rem;
    --lumo-font-size-xs: 0.8125rem;
    --lumo-font-size-s: 0.875rem;
    --lumo-font-size-m: 1rem;
    --lumo-font-size-l: 1.125rem;
    --lumo-font-size-xl: 1.375rem;
    --lumo-font-size-xxl: 1.75rem;
    --lumo-font-size-xxxl: 2.5rem;

    /* Line heights */
    --lumo-line-height-xs: 1.25;
    --lumo-line-height-s: 1.375;
    --lumo-line-height-m: 1.625;
  }
`,ao=_`
  body,
  :host {
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-m);
    line-height: var(--lumo-line-height-m);
    -webkit-text-size-adjust: 100%;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  small,
  [theme~='font-size-s'] {
    font-size: var(--lumo-font-size-s);
    line-height: var(--lumo-line-height-s);
  }

  [theme~='font-size-xs'] {
    font-size: var(--lumo-font-size-xs);
    line-height: var(--lumo-line-height-xs);
  }

  :where(h1, h2, h3, h4, h5, h6) {
    font-weight: 600;
    line-height: var(--lumo-line-height-xs);
    margin-block: 0;
  }

  :where(h1) {
    font-size: var(--lumo-font-size-xxxl);
  }

  :where(h2) {
    font-size: var(--lumo-font-size-xxl);
  }

  :where(h3) {
    font-size: var(--lumo-font-size-xl);
  }

  :where(h4) {
    font-size: var(--lumo-font-size-l);
  }

  :where(h5) {
    font-size: var(--lumo-font-size-m);
  }

  :where(h6) {
    font-size: var(--lumo-font-size-xs);
    text-transform: uppercase;
    letter-spacing: 0.03em;
  }

  p,
  blockquote {
    margin-top: 0.5em;
    margin-bottom: 0.75em;
  }

  a {
    text-decoration: none;
  }

  a:where(:any-link):hover {
    text-decoration: underline;
  }

  hr {
    display: block;
    align-self: stretch;
    height: 1px;
    border: 0;
    padding: 0;
    margin: var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2);
    background-color: var(--lumo-contrast-10pct);
  }

  blockquote {
    border-left: 2px solid var(--lumo-contrast-30pct);
  }

  b,
  strong {
    font-weight: 600;
  }

  /* RTL specific styles */
  blockquote[dir='rtl'] {
    border-left: none;
    border-right: 2px solid var(--lumo-contrast-30pct);
  }
`;g("",ao,{moduleId:"lumo-typography"});O("typography-props",Ri);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Hi=_`
  :host {
    /* Square */
    --lumo-space-xs: 0.25rem;
    --lumo-space-s: 0.5rem;
    --lumo-space-m: 1rem;
    --lumo-space-l: 1.5rem;
    --lumo-space-xl: 2.5rem;

    /* Wide */
    --lumo-space-wide-xs: calc(var(--lumo-space-xs) / 2) var(--lumo-space-xs);
    --lumo-space-wide-s: calc(var(--lumo-space-s) / 2) var(--lumo-space-s);
    --lumo-space-wide-m: calc(var(--lumo-space-m) / 2) var(--lumo-space-m);
    --lumo-space-wide-l: calc(var(--lumo-space-l) / 2) var(--lumo-space-l);
    --lumo-space-wide-xl: calc(var(--lumo-space-xl) / 2) var(--lumo-space-xl);

    /* Tall */
    --lumo-space-tall-xs: var(--lumo-space-xs) calc(var(--lumo-space-xs) / 2);
    --lumo-space-tall-s: var(--lumo-space-s) calc(var(--lumo-space-s) / 2);
    --lumo-space-tall-m: var(--lumo-space-m) calc(var(--lumo-space-m) / 2);
    --lumo-space-tall-l: var(--lumo-space-l) calc(var(--lumo-space-l) / 2);
    --lumo-space-tall-xl: var(--lumo-space-xl) calc(var(--lumo-space-xl) / 2);
  }
`;O("spacing-props",Hi);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const lo=_`
  :host {
    top: var(--lumo-space-m);
    right: var(--lumo-space-m);
    bottom: var(--lumo-space-m);
    left: var(--lumo-space-m);
    /* Workaround for Edge issue (only on Surface), where an overflowing vaadin-list-box inside vaadin-select-overlay makes the overlay transparent */
    /* stylelint-disable-next-line */
    outline: 0px solid transparent;
  }

  [part='overlay'] {
    background-color: var(--lumo-base-color);
    background-image: linear-gradient(var(--lumo-tint-5pct), var(--lumo-tint-5pct));
    border-radius: var(--lumo-border-radius-m);
    box-shadow: 0 0 0 1px var(--lumo-shade-5pct), var(--lumo-box-shadow-m);
    color: var(--lumo-body-text-color);
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-m);
    font-weight: 400;
    line-height: var(--lumo-line-height-m);
    letter-spacing: 0;
    text-transform: none;
    -webkit-text-size-adjust: 100%;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  [part='content'] {
    padding: var(--lumo-space-xs);
  }

  [part='backdrop'] {
    background-color: var(--lumo-shade-20pct);
    animation: 0.2s lumo-overlay-backdrop-enter both;
    will-change: opacity;
  }

  @keyframes lumo-overlay-backdrop-enter {
    0% {
      opacity: 0;
    }
  }

  :host([closing]) [part='backdrop'] {
    animation: 0.2s lumo-overlay-backdrop-exit both;
  }

  @keyframes lumo-overlay-backdrop-exit {
    100% {
      opacity: 0;
    }
  }

  @keyframes lumo-overlay-dummy-animation {
    0% {
      opacity: 1;
    }

    100% {
      opacity: 1;
    }
  }
`;g("",lo,{moduleId:"lumo-overlay"});const Bi=_`
  :host {
    --vaadin-tooltip-offset-top: var(--lumo-space-xs);
    --vaadin-tooltip-offset-bottom: var(--lumo-space-xs);
    --vaadin-tooltip-offset-start: var(--lumo-space-xs);
    --vaadin-tooltip-offset-end: var(--lumo-space-xs);
  }

  [part='overlay'] {
    background: var(--lumo-base-color) linear-gradient(var(--lumo-contrast-5pct), var(--lumo-contrast-5pct));
    color: var(--lumo-body-text-color);
    font-size: var(--lumo-font-size-xs);
    line-height: var(--lumo-line-height-s);
  }

  [part='content'] {
    padding: var(--lumo-space-xs) var(--lumo-space-s);
  }
`;g("vaadin-tooltip-overlay",[lo,Bi],{moduleId:"lumo-tooltip-overlay"});/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/window.JSCompiler_renameProperty=function(o,e){return o};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let Vi=/(url\()([^)]*)(\))/g,Ui=/(^\/[^\/])|(^#)|(^[\w-\d]*:)/,ut,A;function ot(o,e){if(o&&Ui.test(o)||o==="//")return o;if(ut===void 0){ut=!1;try{const t=new URL("b","http://a");t.pathname="c%20d",ut=t.href==="http://a/c%20d"}catch{}}if(e||(e=document.baseURI||window.location.href),ut)try{return new URL(o,e).href}catch{return o}return A||(A=document.implementation.createHTMLDocument("temp"),A.base=A.createElement("base"),A.head.appendChild(A.base),A.anchor=A.createElement("a"),A.body.appendChild(A.anchor)),A.base.href=e,A.anchor.href=o,A.anchor.href||o}function ne(o,e){return o.replace(Vi,function(t,i,r,s){return i+"'"+ot(r.replace(/["']/g,""),e)+"'"+s})}function ae(o){return o.substring(0,o.lastIndexOf("/")+1)}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const ji=!window.ShadyDOM||!window.ShadyDOM.inUse;!window.ShadyCSS||window.ShadyCSS.nativeCss;const qi=ji&&"adoptedStyleSheets"in Document.prototype&&"replaceSync"in CSSStyleSheet.prototype&&(()=>{try{const o=new CSSStyleSheet;o.replaceSync("");const e=document.createElement("div");return e.attachShadow({mode:"open"}),e.shadowRoot.adoptedStyleSheets=[o],e.shadowRoot.adoptedStyleSheets[0]===o}catch{return!1}})();let Wi=window.Polymer&&window.Polymer.rootPath||ae(document.baseURI||window.location.href),vt=window.Polymer&&window.Polymer.sanitizeDOMValue||void 0;window.Polymer&&window.Polymer.setPassiveTouchGestures;let Yt=window.Polymer&&window.Polymer.strictTemplatePolicy||!1,Yi=window.Polymer&&window.Polymer.allowTemplateFromDomModule||!1,Gi=window.Polymer&&window.Polymer.legacyOptimizations||!1,$i=window.Polymer&&window.Polymer.legacyWarnings||!1,Ki=window.Polymer&&window.Polymer.syncInitialRender||!1,Gt=window.Polymer&&window.Polymer.legacyUndefined||!1,Ji=window.Polymer&&window.Polymer.orderedComputed||!1,Ae=window.Polymer&&window.Polymer.removeNestedTemplates||!1,Zi=window.Polymer&&window.Polymer.fastDomIf||!1,za=window.Polymer&&window.Polymer.suppressTemplateNotifications||!1;window.Polymer&&window.Polymer.legacyNoObservedAttributes;let Xi=window.Polymer&&window.Polymer.useAdoptedStyleSheetsWithBuiltCSS||!1;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let Qi=0;const v=function(o){let e=o.__mixinApplications;e||(e=new WeakMap,o.__mixinApplications=e);let t=Qi++;function i(r){let s=r.__mixinSet;if(s&&s[t])return r;let n=e,a=n.get(r);if(!a){a=o(r),n.set(r,a);let l=Object.create(a.__mixinSet||s||null);l[t]=!0,a.__mixinSet=l}return a}return i};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let le={},co={};function Ee(o,e){le[o]=co[o.toLowerCase()]=e}function Pe(o){return le[o]||co[o.toLowerCase()]}function tr(o){o.querySelector("style")&&console.warn("dom-module %s has style outside template",o.id)}class W extends HTMLElement{static get observedAttributes(){return["id"]}static import(e,t){if(e){let i=Pe(e);return i&&t?i.querySelector(t):i}return null}attributeChangedCallback(e,t,i,r){t!==i&&this.register()}get assetpath(){if(!this.__assetpath){const e=window.HTMLImports&&HTMLImports.importForElement?HTMLImports.importForElement(this)||document:this.ownerDocument,t=ot(this.getAttribute("assetpath")||"",e.baseURI);this.__assetpath=ae(t)}return this.__assetpath}register(e){if(e=e||this.id,e){if(Yt&&Pe(e)!==void 0)throw Ee(e,null),new Error(`strictTemplatePolicy: dom-module ${e} re-registered`);this.id=e,Ee(e,this),tr(this)}}}W.prototype.modules=le;customElements.define("dom-module",W);/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const er="link[rel=import][type~=css]",or="include",Te="shady-unscoped";function uo(o){return W.import(o)}function Se(o){let e=o.body?o.body:o;const t=ne(e.textContent,o.baseURI),i=document.createElement("style");return i.textContent=t,i}function ir(o){const e=o.trim().split(/\s+/),t=[];for(let i=0;i<e.length;i++)t.push(...rr(e[i]));return t}function rr(o){const e=uo(o);if(!e)return console.warn("Could not find style data in module named",o),[];if(e._styles===void 0){const t=[];t.push(...ho(e));const i=e.querySelector("template");i&&t.push(...de(i,e.assetpath)),e._styles=t}return e._styles}function de(o,e){if(!o._styles){const t=[],i=o.content.querySelectorAll("style");for(let r=0;r<i.length;r++){let s=i[r],n=s.getAttribute(or);n&&t.push(...ir(n).filter(function(a,l,d){return d.indexOf(a)===l})),e&&(s.textContent=ne(s.textContent,e)),t.push(s)}o._styles=t}return o._styles}function sr(o){let e=uo(o);return e?ho(e):[]}function ho(o){const e=[],t=o.querySelectorAll(er);for(let i=0;i<t.length;i++){let r=t[i];if(r.import){const s=r.import,n=r.hasAttribute(Te);if(n&&!s._unscopedStyle){const a=Se(s);a.setAttribute(Te,""),s._unscopedStyle=a}else s._style||(s._style=Se(s));e.push(n?s._unscopedStyle:s._style)}}return e}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const I=window.ShadyDOM&&window.ShadyDOM.noPatch&&window.ShadyDOM.wrap?window.ShadyDOM.wrap:window.ShadyDOM?o=>ShadyDOM.patch(o):o=>o;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/function $t(o){return o.indexOf(".")>=0}function D(o){let e=o.indexOf(".");return e===-1?o:o.slice(0,e)}function nr(o,e){return o.indexOf(e+".")===0}function bt(o,e){return e.indexOf(o+".")===0}function yt(o,e,t){return e+t.slice(o.length)}function tt(o){if(Array.isArray(o)){let e=[];for(let t=0;t<o.length;t++){let i=o[t].toString().split(".");for(let r=0;r<i.length;r++)e.push(i[r])}return e.join(".")}else return o}function po(o){return Array.isArray(o)?tt(o).split("."):o.toString().split(".")}function C(o,e,t){let i=o,r=po(e);for(let s=0;s<r.length;s++){if(!i)return;let n=r[s];i=i[n]}return t&&(t.path=r.join(".")),i}function ke(o,e,t){let i=o,r=po(e),s=r[r.length-1];if(r.length>1){for(let n=0;n<r.length-1;n++){let a=r[n];if(i=i[a],!i)return}i[s]=t}else i[e]=t;return r.join(".")}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const xt={},ar=/-[a-z]/g,lr=/([A-Z])/g;function fo(o){return xt[o]||(xt[o]=o.indexOf("-")<0?o:o.replace(ar,e=>e[1].toUpperCase()))}function Et(o){return xt[o]||(xt[o]=o.replace(lr,"-$1").toLowerCase())}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let dr=0,mo=0,V=[],cr=0,Kt=!1,_o=document.createTextNode("");new window.MutationObserver(ur).observe(_o,{characterData:!0});function ur(){Kt=!1;const o=V.length;for(let e=0;e<o;e++){let t=V[e];if(t)try{t()}catch(i){setTimeout(()=>{throw i})}}V.splice(0,o),mo+=o}const Ma={after(o){return{run(e){return window.setTimeout(e,o)},cancel(e){window.clearTimeout(e)}}},run(o,e){return window.setTimeout(o,e)},cancel(o){window.clearTimeout(o)}},Na={run(o){return window.requestAnimationFrame(o)},cancel(o){window.cancelAnimationFrame(o)}},Ia={run(o){return window.requestIdleCallback?window.requestIdleCallback(o):window.setTimeout(o,16)},cancel(o){window.cancelIdleCallback?window.cancelIdleCallback(o):window.clearTimeout(o)}},hr={run(o){return Kt||(Kt=!0,_o.textContent=cr++),V.push(o),dr++},cancel(o){const e=o-mo;if(e>=0){if(!V[e])throw new Error("invalid async handle: "+o);V[e]=null}}};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const pr=hr,go=v(o=>{class e extends o{static createProperties(i){const r=this.prototype;for(let s in i)s in r||r._createPropertyAccessor(s)}static attributeNameForProperty(i){return i.toLowerCase()}static typeForProperty(i){}_createPropertyAccessor(i,r){this._addPropertyToAttributeMap(i),this.hasOwnProperty(JSCompiler_renameProperty("__dataHasAccessor",this))||(this.__dataHasAccessor=Object.assign({},this.__dataHasAccessor)),this.__dataHasAccessor[i]||(this.__dataHasAccessor[i]=!0,this._definePropertyAccessor(i,r))}_addPropertyToAttributeMap(i){this.hasOwnProperty(JSCompiler_renameProperty("__dataAttributes",this))||(this.__dataAttributes=Object.assign({},this.__dataAttributes));let r=this.__dataAttributes[i];return r||(r=this.constructor.attributeNameForProperty(i),this.__dataAttributes[r]=i),r}_definePropertyAccessor(i,r){Object.defineProperty(this,i,{get(){return this.__data[i]},set:r?function(){}:function(s){this._setPendingProperty(i,s,!0)&&this._invalidateProperties()}})}constructor(){super(),this.__dataEnabled=!1,this.__dataReady=!1,this.__dataInvalid=!1,this.__data={},this.__dataPending=null,this.__dataOld=null,this.__dataInstanceProps=null,this.__dataCounter=0,this.__serializing=!1,this._initializeProperties()}ready(){this.__dataReady=!0,this._flushProperties()}_initializeProperties(){for(let i in this.__dataHasAccessor)this.hasOwnProperty(i)&&(this.__dataInstanceProps=this.__dataInstanceProps||{},this.__dataInstanceProps[i]=this[i],delete this[i])}_initializeInstanceProperties(i){Object.assign(this,i)}_setProperty(i,r){this._setPendingProperty(i,r)&&this._invalidateProperties()}_getProperty(i){return this.__data[i]}_setPendingProperty(i,r,s){let n=this.__data[i],a=this._shouldPropertyChange(i,r,n);return a&&(this.__dataPending||(this.__dataPending={},this.__dataOld={}),this.__dataOld&&!(i in this.__dataOld)&&(this.__dataOld[i]=n),this.__data[i]=r,this.__dataPending[i]=r),a}_isPropertyPending(i){return!!(this.__dataPending&&this.__dataPending.hasOwnProperty(i))}_invalidateProperties(){!this.__dataInvalid&&this.__dataReady&&(this.__dataInvalid=!0,pr.run(()=>{this.__dataInvalid&&(this.__dataInvalid=!1,this._flushProperties())}))}_enableProperties(){this.__dataEnabled||(this.__dataEnabled=!0,this.__dataInstanceProps&&(this._initializeInstanceProperties(this.__dataInstanceProps),this.__dataInstanceProps=null),this.ready())}_flushProperties(){this.__dataCounter++;const i=this.__data,r=this.__dataPending,s=this.__dataOld;this._shouldPropertiesChange(i,r,s)&&(this.__dataPending=null,this.__dataOld=null,this._propertiesChanged(i,r,s)),this.__dataCounter--}_shouldPropertiesChange(i,r,s){return!!r}_propertiesChanged(i,r,s){}_shouldPropertyChange(i,r,s){return s!==r&&(s===s||r===r)}attributeChangedCallback(i,r,s,n){r!==s&&this._attributeToProperty(i,s),super.attributeChangedCallback&&super.attributeChangedCallback(i,r,s,n)}_attributeToProperty(i,r,s){if(!this.__serializing){const n=this.__dataAttributes,a=n&&n[i]||i;this[a]=this._deserializeValue(r,s||this.constructor.typeForProperty(a))}}_propertyToAttribute(i,r,s){this.__serializing=!0,s=arguments.length<3?this[i]:s,this._valueToNodeAttribute(this,s,r||this.constructor.attributeNameForProperty(i)),this.__serializing=!1}_valueToNodeAttribute(i,r,s){const n=this._serializeValue(r);(s==="class"||s==="name"||s==="slot")&&(i=I(i)),n===void 0?i.removeAttribute(s):i.setAttribute(s,n===""&&window.trustedTypes?window.trustedTypes.emptyScript:n)}_serializeValue(i){switch(typeof i){case"boolean":return i?"":void 0;default:return i!=null?i.toString():void 0}}_deserializeValue(i,r){switch(r){case Boolean:return i!==null;case Number:return Number(i);default:return i}}}return e});/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const vo={};let ht=HTMLElement.prototype;for(;ht;){let o=Object.getOwnPropertyNames(ht);for(let e=0;e<o.length;e++)vo[o[e]]=!0;ht=Object.getPrototypeOf(ht)}const fr=window.trustedTypes?o=>trustedTypes.isHTML(o)||trustedTypes.isScript(o)||trustedTypes.isScriptURL(o):()=>!1;function mr(o,e){if(!vo[e]){let t=o[e];t!==void 0&&(o.__data?o._setPendingProperty(e,t):(o.__dataProto?o.hasOwnProperty(JSCompiler_renameProperty("__dataProto",o))||(o.__dataProto=Object.create(o.__dataProto)):o.__dataProto={},o.__dataProto[e]=t))}}const _r=v(o=>{const e=go(o);class t extends e{static createPropertiesForAttributes(){let r=this.observedAttributes;for(let s=0;s<r.length;s++)this.prototype._createPropertyAccessor(fo(r[s]))}static attributeNameForProperty(r){return Et(r)}_initializeProperties(){this.__dataProto&&(this._initializeProtoProperties(this.__dataProto),this.__dataProto=null),super._initializeProperties()}_initializeProtoProperties(r){for(let s in r)this._setProperty(s,r[s])}_ensureAttribute(r,s){const n=this;n.hasAttribute(r)||this._valueToNodeAttribute(n,s,r)}_serializeValue(r){switch(typeof r){case"object":if(r instanceof Date)return r.toString();if(r){if(fr(r))return r;try{return JSON.stringify(r)}catch{return""}}default:return super._serializeValue(r)}}_deserializeValue(r,s){let n;switch(s){case Object:try{n=JSON.parse(r)}catch{n=r}break;case Array:try{n=JSON.parse(r)}catch{n=null,console.warn(`Polymer::Attributes: couldn't decode Array as JSON: ${r}`)}break;case Date:n=isNaN(r)?String(r):Number(r),n=new Date(n);break;default:n=super._deserializeValue(r,s);break}return n}_definePropertyAccessor(r,s){mr(this,r),super._definePropertyAccessor(r,s)}_hasAccessor(r){return this.__dataHasAccessor&&this.__dataHasAccessor[r]}_isPropertyPending(r){return!!(this.__dataPending&&r in this.__dataPending)}}return t});/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const gr={"dom-if":!0,"dom-repeat":!0};let Oe=!1,ze=!1;function vr(){if(!Oe){Oe=!0;const o=document.createElement("textarea");o.placeholder="a",ze=o.placeholder===o.textContent}return ze}function br(o){vr()&&o.localName==="textarea"&&o.placeholder&&o.placeholder===o.textContent&&(o.textContent=null)}const yr=(()=>{const o=window.trustedTypes&&window.trustedTypes.createPolicy("polymer-template-event-attribute-policy",{createScript:e=>e});return(e,t,i)=>{const r=t.getAttribute(i);if(o&&i.startsWith("on-")){e.setAttribute(i,o.createScript(r,i));return}e.setAttribute(i,r)}})();function xr(o){let e=o.getAttribute("is");if(e&&gr[e]){let t=o;for(t.removeAttribute("is"),o=t.ownerDocument.createElement(e),t.parentNode.replaceChild(o,t),o.appendChild(t);t.attributes.length;){const{name:i}=t.attributes[0];yr(o,t,i),t.removeAttribute(i)}}return o}function bo(o,e){let t=e.parentInfo&&bo(o,e.parentInfo);if(t){for(let i=t.firstChild,r=0;i;i=i.nextSibling)if(e.parentIndex===r++)return i}else return o}function wr(o,e,t,i){i.id&&(e[i.id]=t)}function Cr(o,e,t){if(t.events&&t.events.length)for(let i=0,r=t.events,s;i<r.length&&(s=r[i]);i++)o._addMethodEventListenerToNode(e,s.name,s.value,o)}function Ar(o,e,t,i){t.templateInfo&&(e._templateInfo=t.templateInfo,e._parentTemplateInfo=i)}function Er(o,e,t){return o=o._methodHost||o,function(r){o[t]?o[t](r,r.detail):console.warn("listener method `"+t+"` not defined")}}const Pr=v(o=>{class e extends o{static _parseTemplate(i,r){if(!i._templateInfo){let s=i._templateInfo={};s.nodeInfoList=[],s.nestedTemplate=!!r,s.stripWhiteSpace=r&&r.stripWhiteSpace||i.hasAttribute&&i.hasAttribute("strip-whitespace"),this._parseTemplateContent(i,s,{parent:null})}return i._templateInfo}static _parseTemplateContent(i,r,s){return this._parseTemplateNode(i.content,r,s)}static _parseTemplateNode(i,r,s){let n=!1,a=i;return a.localName=="template"&&!a.hasAttribute("preserve-content")?n=this._parseTemplateNestedTemplate(a,r,s)||n:a.localName==="slot"&&(r.hasInsertionPoint=!0),br(a),a.firstChild&&this._parseTemplateChildNodes(a,r,s),a.hasAttributes&&a.hasAttributes()&&(n=this._parseTemplateNodeAttributes(a,r,s)||n),n||s.noted}static _parseTemplateChildNodes(i,r,s){if(!(i.localName==="script"||i.localName==="style"))for(let n=i.firstChild,a=0,l;n;n=l){if(n.localName=="template"&&(n=xr(n)),l=n.nextSibling,n.nodeType===Node.TEXT_NODE){let c=l;for(;c&&c.nodeType===Node.TEXT_NODE;)n.textContent+=c.textContent,l=c.nextSibling,i.removeChild(c),c=l;if(r.stripWhiteSpace&&!n.textContent.trim()){i.removeChild(n);continue}}let d={parentIndex:a,parentInfo:s};this._parseTemplateNode(n,r,d)&&(d.infoIndex=r.nodeInfoList.push(d)-1),n.parentNode&&a++}}static _parseTemplateNestedTemplate(i,r,s){let n=i,a=this._parseTemplate(n,r);return(a.content=n.content.ownerDocument.createDocumentFragment()).appendChild(n.content),s.templateInfo=a,!0}static _parseTemplateNodeAttributes(i,r,s){let n=!1,a=Array.from(i.attributes);for(let l=a.length-1,d;d=a[l];l--)n=this._parseTemplateNodeAttribute(i,r,s,d.name,d.value)||n;return n}static _parseTemplateNodeAttribute(i,r,s,n,a){return n.slice(0,3)==="on-"?(i.removeAttribute(n),s.events=s.events||[],s.events.push({name:n.slice(3),value:a}),!0):n==="id"?(s.id=a,!0):!1}static _contentForTemplate(i){let r=i._templateInfo;return r&&r.content||i.content}_stampTemplate(i,r){i&&!i.content&&window.HTMLTemplateElement&&HTMLTemplateElement.decorate&&HTMLTemplateElement.decorate(i),r=r||this.constructor._parseTemplate(i);let s=r.nodeInfoList,n=r.content||i.content,a=document.importNode(n,!0);a.__noInsertionPoint=!r.hasInsertionPoint;let l=a.nodeList=new Array(s.length);a.$={};for(let d=0,c=s.length,u;d<c&&(u=s[d]);d++){let h=l[d]=bo(a,u);wr(this,a.$,h,u),Ar(this,h,u,r),Cr(this,h,u)}return a=a,a}_addMethodEventListenerToNode(i,r,s,n){n=n||i;let a=Er(n,r,s);return this._addEventListenerToNode(i,r,a),a}_addEventListenerToNode(i,r,s){i.addEventListener(r,s)}_removeEventListenerFromNode(i,r,s){i.removeEventListener(r,s)}}return e});/**
 * @fileoverview
 * @suppress {checkPrototypalTypes}
 * @license Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt The complete set of authors may be found
 * at http://polymer.github.io/AUTHORS.txt The complete set of contributors may
 * be found at http://polymer.github.io/CONTRIBUTORS.txt Code distributed by
 * Google as part of the polymer project is also subject to an additional IP
 * rights grant found at http://polymer.github.io/PATENTS.txt
 */let it=0;const rt=[],m={COMPUTE:"__computeEffects",REFLECT:"__reflectEffects",NOTIFY:"__notifyEffects",PROPAGATE:"__propagateEffects",OBSERVE:"__observeEffects",READ_ONLY:"__readOnly"},yo="__computeInfo",Tr=/[A-Z]/;function Ot(o,e,t){let i=o[e];if(!i)i=o[e]={};else if(!o.hasOwnProperty(e)&&(i=o[e]=Object.create(o[e]),t))for(let r in i){let s=i[r],n=i[r]=Array(s.length);for(let a=0;a<s.length;a++)n[a]=s[a]}return i}function et(o,e,t,i,r,s){if(e){let n=!1;const a=it++;for(let l in t){let d=r?D(l):l,c=e[d];if(c)for(let u=0,h=c.length,p;u<h&&(p=c[u]);u++)(!p.info||p.info.lastRun!==a)&&(!r||ce(l,p.trigger))&&(p.info&&(p.info.lastRun=a),p.fn(o,l,t,i,p.info,r,s),n=!0)}return n}return!1}function Sr(o,e,t,i,r,s,n,a){let l=!1,d=n?D(i):i,c=e[d];if(c)for(let u=0,h=c.length,p;u<h&&(p=c[u]);u++)(!p.info||p.info.lastRun!==t)&&(!n||ce(i,p.trigger))&&(p.info&&(p.info.lastRun=t),p.fn(o,i,r,s,p.info,n,a),l=!0);return l}function ce(o,e){if(e){let t=e.name;return t==o||!!(e.structured&&nr(t,o))||!!(e.wildcard&&bt(t,o))}else return!0}function Me(o,e,t,i,r){let s=typeof r.method=="string"?o[r.method]:r.method,n=r.property;s?s.call(o,o.__data[n],i[n]):r.dynamicFn||console.warn("observer method `"+r.method+"` not defined")}function kr(o,e,t,i,r){let s=o[m.NOTIFY],n,a=it++;for(let d in e)e[d]&&(s&&Sr(o,s,a,d,t,i,r)||r&&Or(o,d,t))&&(n=!0);let l;n&&(l=o.__dataHost)&&l._invalidateProperties&&l._invalidateProperties()}function Or(o,e,t){let i=D(e);if(i!==e){let r=Et(i)+"-changed";return xo(o,r,t[e],e),!0}return!1}function xo(o,e,t,i){let r={value:t,queueProperty:!0};i&&(r.path=i),I(o).dispatchEvent(new CustomEvent(e,{detail:r}))}function zr(o,e,t,i,r,s){let a=(s?D(e):e)!=e?e:null,l=a?C(o,a):o.__data[e];a&&l===void 0&&(l=t[e]),xo(o,r.eventName,l,a)}function Mr(o,e,t,i,r){let s,n=o.detail,a=n&&n.path;a?(i=yt(t,i,a),s=n&&n.value):s=o.currentTarget[t],s=r?!s:s,(!e[m.READ_ONLY]||!e[m.READ_ONLY][i])&&e._setPendingPropertyOrPath(i,s,!0,!!a)&&(!n||!n.queueProperty)&&e._invalidateProperties()}function Nr(o,e,t,i,r){let s=o.__data[e];vt&&(s=vt(s,r.attrName,"attribute",o)),o._propertyToAttribute(e,r.attrName,s)}function Ir(o,e,t,i){let r=o[m.COMPUTE];if(r)if(Ji){it++;const s=Dr(o),n=[];for(let l in e)Ne(l,r,n,s,i);let a;for(;a=n.shift();)wo(o,"",e,t,a)&&Ne(a.methodInfo,r,n,s,i);Object.assign(t,o.__dataOld),Object.assign(e,o.__dataPending),o.__dataPending=null}else{let s=e;for(;et(o,r,s,t,i);)Object.assign(t,o.__dataOld),Object.assign(e,o.__dataPending),s=o.__dataPending,o.__dataPending=null}}const Lr=(o,e,t)=>{let i=0,r=e.length-1,s=-1;for(;i<=r;){const n=i+r>>1,a=t.get(e[n].methodInfo)-t.get(o.methodInfo);if(a<0)i=n+1;else if(a>0)r=n-1;else{s=n;break}}s<0&&(s=r+1),e.splice(s,0,o)},Ne=(o,e,t,i,r)=>{const s=r?D(o):o,n=e[s];if(n)for(let a=0;a<n.length;a++){const l=n[a];l.info.lastRun!==it&&(!r||ce(o,l.trigger))&&(l.info.lastRun=it,Lr(l.info,t,i))}};function Dr(o){let e=o.constructor.__orderedComputedDeps;if(!e){e=new Map;const t=o[m.COMPUTE];let{counts:i,ready:r,total:s}=Fr(o),n;for(;n=r.shift();){e.set(n,e.size);const a=t[n];a&&a.forEach(l=>{const d=l.info.methodInfo;--s,--i[d]===0&&r.push(d)})}s!==0&&console.warn(`Computed graph for ${o.localName} incomplete; circular?`),o.constructor.__orderedComputedDeps=e}return e}function Fr(o){const e=o[yo],t={},i=o[m.COMPUTE],r=[];let s=0;for(let n in e){const a=e[n];s+=t[n]=a.args.filter(l=>!l.literal).length+(a.dynamicFn?1:0)}for(let n in i)e[n]||r.push(n);return{counts:t,ready:r,total:s}}function wo(o,e,t,i,r){let s=Jt(o,e,t,i,r);if(s===rt)return!1;let n=r.methodInfo;return o.__dataHasAccessor&&o.__dataHasAccessor[n]?o._setPendingProperty(n,s,!0):(o[n]=s,!1)}function Rr(o,e,t){let i=o.__dataLinkedPaths;if(i){let r;for(let s in i){let n=i[s];bt(s,e)?(r=yt(s,n,e),o._setPendingPropertyOrPath(r,t,!0,!0)):bt(n,e)&&(r=yt(n,s,e),o._setPendingPropertyOrPath(r,t,!0,!0))}}}function zt(o,e,t,i,r,s,n){t.bindings=t.bindings||[];let a={kind:i,target:r,parts:s,literal:n,isCompound:s.length!==1};if(t.bindings.push(a),jr(a)){let{event:d,negate:c}=a.parts[0];a.listenerEvent=d||Et(r)+"-changed",a.listenerNegate=c}let l=e.nodeInfoList.length;for(let d=0;d<a.parts.length;d++){let c=a.parts[d];c.compoundIndex=d,Hr(o,e,a,c,l)}}function Hr(o,e,t,i,r){if(!i.literal)if(t.kind==="attribute"&&t.target[0]==="-")console.warn("Cannot set attribute "+t.target+' because "-" is not a valid attribute starting character');else{let s=i.dependencies,n={index:r,binding:t,part:i,evaluator:o};for(let a=0;a<s.length;a++){let l=s[a];typeof l=="string"&&(l=Ao(l),l.wildcard=!0),o._addTemplatePropertyEffect(e,l.rootProperty,{fn:Br,info:n,trigger:l})}}}function Br(o,e,t,i,r,s,n){let a=n[r.index],l=r.binding,d=r.part;if(s&&d.source&&e.length>d.source.length&&l.kind=="property"&&!l.isCompound&&a.__isPropertyEffectsClient&&a.__dataHasAccessor&&a.__dataHasAccessor[l.target]){let c=t[e];e=yt(d.source,l.target,e),a._setPendingPropertyOrPath(e,c,!1,!0)&&o._enqueueClient(a)}else{let c=r.evaluator._evaluateBinding(o,d,e,t,i,s);c!==rt&&Vr(o,a,l,d,c)}}function Vr(o,e,t,i,r){if(r=Ur(e,r,t,i),vt&&(r=vt(r,t.target,t.kind,e)),t.kind=="attribute")o._valueToNodeAttribute(e,r,t.target);else{let s=t.target;e.__isPropertyEffectsClient&&e.__dataHasAccessor&&e.__dataHasAccessor[s]?(!e[m.READ_ONLY]||!e[m.READ_ONLY][s])&&e._setPendingProperty(s,r)&&o._enqueueClient(e):o._setUnmanagedPropertyToNode(e,s,r)}}function Ur(o,e,t,i){if(t.isCompound){let r=o.__dataCompoundStorage[t.target];r[i.compoundIndex]=e,e=r.join("")}return t.kind!=="attribute"&&(t.target==="textContent"||t.target==="value"&&(o.localName==="input"||o.localName==="textarea"))&&(e=e??""),e}function jr(o){return!!o.target&&o.kind!="attribute"&&o.kind!="text"&&!o.isCompound&&o.parts[0].mode==="{"}function qr(o,e){let{nodeList:t,nodeInfoList:i}=e;if(i.length)for(let r=0;r<i.length;r++){let s=i[r],n=t[r],a=s.bindings;if(a)for(let l=0;l<a.length;l++){let d=a[l];Wr(n,d),Yr(n,o,d)}n.__dataHost=o}}function Wr(o,e){if(e.isCompound){let t=o.__dataCompoundStorage||(o.__dataCompoundStorage={}),i=e.parts,r=new Array(i.length);for(let n=0;n<i.length;n++)r[n]=i[n].literal;let s=e.target;t[s]=r,e.literal&&e.kind=="property"&&(s==="className"&&(o=I(o)),o[s]=e.literal)}}function Yr(o,e,t){if(t.listenerEvent){let i=t.parts[0];o.addEventListener(t.listenerEvent,function(r){Mr(r,e,t.target,i.source,i.negate)})}}function Ie(o,e,t,i,r,s){s=e.static||s&&(typeof s!="object"||s[e.methodName]);let n={methodName:e.methodName,args:e.args,methodInfo:r,dynamicFn:s};for(let a=0,l;a<e.args.length&&(l=e.args[a]);a++)l.literal||o._addPropertyEffect(l.rootProperty,t,{fn:i,info:n,trigger:l});return s&&o._addPropertyEffect(e.methodName,t,{fn:i,info:n}),n}function Jt(o,e,t,i,r){let s=o._methodHost||o,n=s[r.methodName];if(n){let a=o._marshalArgs(r.args,e,t);return a===rt?rt:n.apply(s,a)}else r.dynamicFn||console.warn("method `"+r.methodName+"` not defined")}const Gr=[],Co="(?:[a-zA-Z_$][\\w.:$\\-*]*)",$r="(?:[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?)",Kr="(?:'(?:[^'\\\\]|\\\\.)*')",Jr='(?:"(?:[^"\\\\]|\\\\.)*")',Zr="(?:"+Kr+"|"+Jr+")",Le="(?:("+Co+"|"+$r+"|"+Zr+")\\s*)",Xr="(?:"+Le+"(?:,\\s*"+Le+")*)",Qr="(?:\\(\\s*(?:"+Xr+"?)\\)\\s*)",ts="("+Co+"\\s*"+Qr+"?)",es="(\\[\\[|{{)\\s*",os="(?:]]|}})",is="(?:(!)\\s*)?",rs=es+is+ts+os,De=new RegExp(rs,"g");function Fe(o){let e="";for(let t=0;t<o.length;t++){let i=o[t].literal;e+=i||""}return e}function Mt(o){let e=o.match(/([^\s]+?)\(([\s\S]*)\)/);if(e){let i={methodName:e[1],static:!0,args:Gr};if(e[2].trim()){let r=e[2].replace(/\\,/g,"&comma;").split(",");return ss(r,i)}else return i}return null}function ss(o,e){return e.args=o.map(function(t){let i=Ao(t);return i.literal||(e.static=!1),i},this),e}function Ao(o){let e=o.trim().replace(/&comma;/g,",").replace(/\\(.)/g,"$1"),t={name:e,value:"",literal:!1},i=e[0];switch(i==="-"&&(i=e[1]),i>="0"&&i<="9"&&(i="#"),i){case"'":case'"':t.value=e.slice(1,-1),t.literal=!0;break;case"#":t.value=Number(e),t.literal=!0;break}return t.literal||(t.rootProperty=D(e),t.structured=$t(e),t.structured&&(t.wildcard=e.slice(-2)==".*",t.wildcard&&(t.name=e.slice(0,-2)))),t}function Re(o,e,t){let i=C(o,t);return i===void 0&&(i=e[t]),i}function Eo(o,e,t,i){const r={indexSplices:i};Gt&&!o._overrideLegacyUndefined&&(e.splices=r),o.notifyPath(t+".splices",r),o.notifyPath(t+".length",e.length),Gt&&!o._overrideLegacyUndefined&&(r.indexSplices=[])}function G(o,e,t,i,r,s){Eo(o,e,t,[{index:i,addedCount:r,removed:s,object:e,type:"splice"}])}function ns(o){return o[0].toUpperCase()+o.substring(1)}const as=v(o=>{const e=Pr(_r(o));class t extends e{constructor(){super(),this.__isPropertyEffectsClient=!0,this.__dataClientsReady,this.__dataPendingClients,this.__dataToNotify,this.__dataLinkedPaths,this.__dataHasPaths,this.__dataCompoundStorage,this.__dataHost,this.__dataTemp,this.__dataClientsInitialized,this.__data,this.__dataPending,this.__dataOld,this.__computeEffects,this.__computeInfo,this.__reflectEffects,this.__notifyEffects,this.__propagateEffects,this.__observeEffects,this.__readOnly,this.__templateInfo,this._overrideLegacyUndefined}get PROPERTY_EFFECT_TYPES(){return m}_initializeProperties(){super._initializeProperties(),this._registerHost(),this.__dataClientsReady=!1,this.__dataPendingClients=null,this.__dataToNotify=null,this.__dataLinkedPaths=null,this.__dataHasPaths=!1,this.__dataCompoundStorage=this.__dataCompoundStorage||null,this.__dataHost=this.__dataHost||null,this.__dataTemp={},this.__dataClientsInitialized=!1}_registerHost(){if($.length){let r=$[$.length-1];r._enqueueClient(this),this.__dataHost=r}}_initializeProtoProperties(r){this.__data=Object.create(r),this.__dataPending=Object.create(r),this.__dataOld={}}_initializeInstanceProperties(r){let s=this[m.READ_ONLY];for(let n in r)(!s||!s[n])&&(this.__dataPending=this.__dataPending||{},this.__dataOld=this.__dataOld||{},this.__data[n]=this.__dataPending[n]=r[n])}_addPropertyEffect(r,s,n){this._createPropertyAccessor(r,s==m.READ_ONLY);let a=Ot(this,s,!0)[r];a||(a=this[s][r]=[]),a.push(n)}_removePropertyEffect(r,s,n){let a=Ot(this,s,!0)[r],l=a.indexOf(n);l>=0&&a.splice(l,1)}_hasPropertyEffect(r,s){let n=this[s];return!!(n&&n[r])}_hasReadOnlyEffect(r){return this._hasPropertyEffect(r,m.READ_ONLY)}_hasNotifyEffect(r){return this._hasPropertyEffect(r,m.NOTIFY)}_hasReflectEffect(r){return this._hasPropertyEffect(r,m.REFLECT)}_hasComputedEffect(r){return this._hasPropertyEffect(r,m.COMPUTE)}_setPendingPropertyOrPath(r,s,n,a){if(a||D(Array.isArray(r)?r[0]:r)!==r){if(!a){let l=C(this,r);if(r=ke(this,r,s),!r||!super._shouldPropertyChange(r,s,l))return!1}if(this.__dataHasPaths=!0,this._setPendingProperty(r,s,n))return Rr(this,r,s),!0}else{if(this.__dataHasAccessor&&this.__dataHasAccessor[r])return this._setPendingProperty(r,s,n);this[r]=s}return!1}_setUnmanagedPropertyToNode(r,s,n){(n!==r[s]||typeof n=="object")&&(s==="className"&&(r=I(r)),r[s]=n)}_setPendingProperty(r,s,n){let a=this.__dataHasPaths&&$t(r),l=a?this.__dataTemp:this.__data;return this._shouldPropertyChange(r,s,l[r])?(this.__dataPending||(this.__dataPending={},this.__dataOld={}),r in this.__dataOld||(this.__dataOld[r]=this.__data[r]),a?this.__dataTemp[r]=s:this.__data[r]=s,this.__dataPending[r]=s,(a||this[m.NOTIFY]&&this[m.NOTIFY][r])&&(this.__dataToNotify=this.__dataToNotify||{},this.__dataToNotify[r]=n),!0):!1}_setProperty(r,s){this._setPendingProperty(r,s,!0)&&this._invalidateProperties()}_invalidateProperties(){this.__dataReady&&this._flushProperties()}_enqueueClient(r){this.__dataPendingClients=this.__dataPendingClients||[],r!==this&&this.__dataPendingClients.push(r)}_flushClients(){this.__dataClientsReady?this.__enableOrFlushClients():(this.__dataClientsReady=!0,this._readyClients(),this.__dataReady=!0)}__enableOrFlushClients(){let r=this.__dataPendingClients;if(r){this.__dataPendingClients=null;for(let s=0;s<r.length;s++){let n=r[s];n.__dataEnabled?n.__dataPending&&n._flushProperties():n._enableProperties()}}}_readyClients(){this.__enableOrFlushClients()}setProperties(r,s){for(let n in r)(s||!this[m.READ_ONLY]||!this[m.READ_ONLY][n])&&this._setPendingPropertyOrPath(n,r[n],!0);this._invalidateProperties()}ready(){this._flushProperties(),this.__dataClientsReady||this._flushClients(),this.__dataPending&&this._flushProperties()}_propertiesChanged(r,s,n){let a=this.__dataHasPaths;this.__dataHasPaths=!1;let l;Ir(this,s,n,a),l=this.__dataToNotify,this.__dataToNotify=null,this._propagatePropertyChanges(s,n,a),this._flushClients(),et(this,this[m.REFLECT],s,n,a),et(this,this[m.OBSERVE],s,n,a),l&&kr(this,l,s,n,a),this.__dataCounter==1&&(this.__dataTemp={})}_propagatePropertyChanges(r,s,n){this[m.PROPAGATE]&&et(this,this[m.PROPAGATE],r,s,n),this.__templateInfo&&this._runEffectsForTemplate(this.__templateInfo,r,s,n)}_runEffectsForTemplate(r,s,n,a){const l=(d,c)=>{et(this,r.propertyEffects,d,n,c,r.nodeList);for(let u=r.firstChild;u;u=u.nextSibling)this._runEffectsForTemplate(u,d,n,c)};r.runEffects?r.runEffects(l,s,a):l(s,a)}linkPaths(r,s){r=tt(r),s=tt(s),this.__dataLinkedPaths=this.__dataLinkedPaths||{},this.__dataLinkedPaths[r]=s}unlinkPaths(r){r=tt(r),this.__dataLinkedPaths&&delete this.__dataLinkedPaths[r]}notifySplices(r,s){let n={path:""},a=C(this,r,n);Eo(this,a,n.path,s)}get(r,s){return C(s||this,r)}set(r,s,n){n?ke(n,r,s):(!this[m.READ_ONLY]||!this[m.READ_ONLY][r])&&this._setPendingPropertyOrPath(r,s,!0)&&this._invalidateProperties()}push(r,...s){let n={path:""},a=C(this,r,n),l=a.length,d=a.push(...s);return s.length&&G(this,a,n.path,l,s.length,[]),d}pop(r){let s={path:""},n=C(this,r,s),a=!!n.length,l=n.pop();return a&&G(this,n,s.path,n.length,0,[l]),l}splice(r,s,n,...a){let l={path:""},d=C(this,r,l);s<0?s=d.length-Math.floor(-s):s&&(s=Math.floor(s));let c;return arguments.length===2?c=d.splice(s):c=d.splice(s,n,...a),(a.length||c.length)&&G(this,d,l.path,s,a.length,c),c}shift(r){let s={path:""},n=C(this,r,s),a=!!n.length,l=n.shift();return a&&G(this,n,s.path,0,0,[l]),l}unshift(r,...s){let n={path:""},a=C(this,r,n),l=a.unshift(...s);return s.length&&G(this,a,n.path,0,s.length,[]),l}notifyPath(r,s){let n;if(arguments.length==1){let a={path:""};s=C(this,r,a),n=a.path}else Array.isArray(r)?n=tt(r):n=r;this._setPendingPropertyOrPath(n,s,!0,!0)&&this._invalidateProperties()}_createReadOnlyProperty(r,s){this._addPropertyEffect(r,m.READ_ONLY),s&&(this["_set"+ns(r)]=function(n){this._setProperty(r,n)})}_createPropertyObserver(r,s,n){let a={property:r,method:s,dynamicFn:!!n};this._addPropertyEffect(r,m.OBSERVE,{fn:Me,info:a,trigger:{name:r}}),n&&this._addPropertyEffect(s,m.OBSERVE,{fn:Me,info:a,trigger:{name:s}})}_createMethodObserver(r,s){let n=Mt(r);if(!n)throw new Error("Malformed observer expression '"+r+"'");Ie(this,n,m.OBSERVE,Jt,null,s)}_createNotifyingProperty(r){this._addPropertyEffect(r,m.NOTIFY,{fn:zr,info:{eventName:Et(r)+"-changed",property:r}})}_createReflectedProperty(r){let s=this.constructor.attributeNameForProperty(r);s[0]==="-"?console.warn("Property "+r+" cannot be reflected to attribute "+s+' because "-" is not a valid starting attribute name. Use a lowercase first letter for the property instead.'):this._addPropertyEffect(r,m.REFLECT,{fn:Nr,info:{attrName:s}})}_createComputedProperty(r,s,n){let a=Mt(s);if(!a)throw new Error("Malformed computed expression '"+s+"'");const l=Ie(this,a,m.COMPUTE,wo,r,n);Ot(this,yo)[r]=l}_marshalArgs(r,s,n){const a=this.__data,l=[];for(let d=0,c=r.length;d<c;d++){let{name:u,structured:h,wildcard:p,value:f,literal:x}=r[d];if(!x)if(p){const w=bt(u,s),b=Re(a,n,w?s:u);f={path:w?s:u,value:b,base:w?C(a,u):b}}else f=h?Re(a,n,u):a[u];if(Gt&&!this._overrideLegacyUndefined&&f===void 0&&r.length>1)return rt;l[d]=f}return l}static addPropertyEffect(r,s,n){this.prototype._addPropertyEffect(r,s,n)}static createPropertyObserver(r,s,n){this.prototype._createPropertyObserver(r,s,n)}static createMethodObserver(r,s){this.prototype._createMethodObserver(r,s)}static createNotifyingProperty(r){this.prototype._createNotifyingProperty(r)}static createReadOnlyProperty(r,s){this.prototype._createReadOnlyProperty(r,s)}static createReflectedProperty(r){this.prototype._createReflectedProperty(r)}static createComputedProperty(r,s,n){this.prototype._createComputedProperty(r,s,n)}static bindTemplate(r){return this.prototype._bindTemplate(r)}_bindTemplate(r,s){let n=this.constructor._parseTemplate(r),a=this.__preBoundTemplateInfo==n;if(!a)for(let l in n.propertyEffects)this._createPropertyAccessor(l);if(s)if(n=Object.create(n),n.wasPreBound=a,!this.__templateInfo)this.__templateInfo=n;else{const l=r._parentTemplateInfo||this.__templateInfo,d=l.lastChild;n.parent=l,l.lastChild=n,n.previousSibling=d,d?d.nextSibling=n:l.firstChild=n}else this.__preBoundTemplateInfo=n;return n}static _addTemplatePropertyEffect(r,s,n){let a=r.hostProps=r.hostProps||{};a[s]=!0;let l=r.propertyEffects=r.propertyEffects||{};(l[s]=l[s]||[]).push(n)}_stampTemplate(r,s){s=s||this._bindTemplate(r,!0),$.push(this);let n=super._stampTemplate(r,s);if($.pop(),s.nodeList=n.nodeList,!s.wasPreBound){let a=s.childNodes=[];for(let l=n.firstChild;l;l=l.nextSibling)a.push(l)}return n.templateInfo=s,qr(this,s),this.__dataClientsReady&&(this._runEffectsForTemplate(s,this.__data,null,!1),this._flushClients()),n}_removeBoundDom(r){const s=r.templateInfo,{previousSibling:n,nextSibling:a,parent:l}=s;n?n.nextSibling=a:l&&(l.firstChild=a),a?a.previousSibling=n:l&&(l.lastChild=n),s.nextSibling=s.previousSibling=null;let d=s.childNodes;for(let c=0;c<d.length;c++){let u=d[c];I(I(u).parentNode).removeChild(u)}}static _parseTemplateNode(r,s,n){let a=e._parseTemplateNode.call(this,r,s,n);if(r.nodeType===Node.TEXT_NODE){let l=this._parseBindings(r.textContent,s);l&&(r.textContent=Fe(l)||" ",zt(this,s,n,"text","textContent",l),a=!0)}return a}static _parseTemplateNodeAttribute(r,s,n,a,l){let d=this._parseBindings(l,s);if(d){let c=a,u="property";Tr.test(a)?u="attribute":a[a.length-1]=="$"&&(a=a.slice(0,-1),u="attribute");let h=Fe(d);return h&&u=="attribute"&&(a=="class"&&r.hasAttribute("class")&&(h+=" "+r.getAttribute(a)),r.setAttribute(a,h)),u=="attribute"&&c=="disable-upgrade$"&&r.setAttribute(a,""),r.localName==="input"&&c==="value"&&r.setAttribute(c,""),r.removeAttribute(c),u==="property"&&(a=fo(a)),zt(this,s,n,u,a,d,h),!0}else return e._parseTemplateNodeAttribute.call(this,r,s,n,a,l)}static _parseTemplateNestedTemplate(r,s,n){let a=e._parseTemplateNestedTemplate.call(this,r,s,n);const l=r.parentNode,d=n.templateInfo,c=l.localName==="dom-if",u=l.localName==="dom-repeat";Ae&&(c||u)&&(l.removeChild(r),n=n.parentInfo,n.templateInfo=d,n.noted=!0,a=!1);let h=d.hostProps;if(Zi&&c)h&&(s.hostProps=Object.assign(s.hostProps||{},h),Ae||(n.parentInfo.noted=!0));else{let p="{";for(let f in h){let x=[{mode:p,source:f,dependencies:[f],hostProp:!0}];zt(this,s,n,"property","_host_"+f,x)}}return a}static _parseBindings(r,s){let n=[],a=0,l;for(;(l=De.exec(r))!==null;){l.index>a&&n.push({literal:r.slice(a,l.index)});let d=l[1][0],c=!!l[2],u=l[3].trim(),h=!1,p="",f=-1;d=="{"&&(f=u.indexOf("::"))>0&&(p=u.substring(f+2),u=u.substring(0,f),h=!0);let x=Mt(u),w=[];if(x){let{args:b,methodName:y}=x;for(let kt=0;kt<b.length;kt++){let we=b[kt];we.literal||w.push(we)}let R=s.dynamicFns;(R&&R[y]||x.static)&&(w.push(y),x.dynamicFn=!0)}else w.push(u);n.push({source:u,mode:d,negate:c,customEvent:h,signature:x,dependencies:w,event:p}),a=De.lastIndex}if(a&&a<r.length){let d=r.substring(a);d&&n.push({literal:d})}return n.length?n:null}static _evaluateBinding(r,s,n,a,l,d){let c;return s.signature?c=Jt(r,n,a,l,s.signature):n!=s.source?c=C(r,s.source):d&&$t(n)?c=C(r,n):c=r.__data[n],s.negate&&(c=!c),c}}return t}),$=[];/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*//**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/function ls(o){const e={};for(let t in o){const i=o[t];e[t]=typeof i=="function"?{type:i}:i}return e}const ds=v(o=>{const e=go(o);function t(s){const n=Object.getPrototypeOf(s);return n.prototype instanceof r?n:null}function i(s){if(!s.hasOwnProperty(JSCompiler_renameProperty("__ownProperties",s))){let n=null;if(s.hasOwnProperty(JSCompiler_renameProperty("properties",s))){const a=s.properties;a&&(n=ls(a))}s.__ownProperties=n}return s.__ownProperties}class r extends e{static get observedAttributes(){if(!this.hasOwnProperty(JSCompiler_renameProperty("__observedAttributes",this))){this.prototype;const n=this._properties;this.__observedAttributes=n?Object.keys(n).map(a=>this.prototype._addPropertyToAttributeMap(a)):[]}return this.__observedAttributes}static finalize(){if(!this.hasOwnProperty(JSCompiler_renameProperty("__finalized",this))){const n=t(this);n&&n.finalize(),this.__finalized=!0,this._finalizeClass()}}static _finalizeClass(){const n=i(this);n&&this.createProperties(n)}static get _properties(){if(!this.hasOwnProperty(JSCompiler_renameProperty("__properties",this))){const n=t(this);this.__properties=Object.assign({},n&&n._properties,i(this))}return this.__properties}static typeForProperty(n){const a=this._properties[n];return a&&a.type}_initializeProperties(){this.constructor.finalize(),super._initializeProperties()}connectedCallback(){super.connectedCallback&&super.connectedCallback(),this._enableProperties()}disconnectedCallback(){super.disconnectedCallback&&super.disconnectedCallback()}}return r});/**
 * @fileoverview
 * @suppress {checkPrototypalTypes}
 * @license Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt The complete set of authors may be found
 * at http://polymer.github.io/AUTHORS.txt The complete set of contributors may
 * be found at http://polymer.github.io/CONTRIBUTORS.txt Code distributed by
 * Google as part of the polymer project is also subject to an additional IP
 * rights grant found at http://polymer.github.io/PATENTS.txt
 */const cs="3.5.1",He=window.ShadyCSS&&window.ShadyCSS.cssBuild,us=v(o=>{const e=ds(as(o));function t(l){if(!l.hasOwnProperty(JSCompiler_renameProperty("__propertyDefaults",l))){l.__propertyDefaults=null;let d=l._properties;for(let c in d){let u=d[c];"value"in u&&(l.__propertyDefaults=l.__propertyDefaults||{},l.__propertyDefaults[c]=u)}}return l.__propertyDefaults}function i(l){return l.hasOwnProperty(JSCompiler_renameProperty("__ownObservers",l))||(l.__ownObservers=l.hasOwnProperty(JSCompiler_renameProperty("observers",l))?l.observers:null),l.__ownObservers}function r(l,d,c,u){c.computed&&(c.readOnly=!0),c.computed&&(l._hasReadOnlyEffect(d)?console.warn(`Cannot redefine computed property '${d}'.`):l._createComputedProperty(d,c.computed,u)),c.readOnly&&!l._hasReadOnlyEffect(d)?l._createReadOnlyProperty(d,!c.computed):c.readOnly===!1&&l._hasReadOnlyEffect(d)&&console.warn(`Cannot make readOnly property '${d}' non-readOnly.`),c.reflectToAttribute&&!l._hasReflectEffect(d)?l._createReflectedProperty(d):c.reflectToAttribute===!1&&l._hasReflectEffect(d)&&console.warn(`Cannot make reflected property '${d}' non-reflected.`),c.notify&&!l._hasNotifyEffect(d)?l._createNotifyingProperty(d):c.notify===!1&&l._hasNotifyEffect(d)&&console.warn(`Cannot make notify property '${d}' non-notify.`),c.observer&&l._createPropertyObserver(d,c.observer,u[c.observer]),l._addPropertyToAttributeMap(d)}function s(l,d,c,u){if(!He){const h=d.content.querySelectorAll("style"),p=de(d),f=sr(c),x=d.content.firstElementChild;for(let b=0;b<f.length;b++){let y=f[b];y.textContent=l._processStyleText(y.textContent,u),d.content.insertBefore(y,x)}let w=0;for(let b=0;b<p.length;b++){let y=p[b],R=h[w];R!==y?(y=y.cloneNode(!0),R.parentNode.insertBefore(y,R)):w++,y.textContent=l._processStyleText(y.textContent,u)}}if(window.ShadyCSS&&window.ShadyCSS.prepareTemplate(d,c),Xi&&He&&qi){const h=d.content.querySelectorAll("style");if(h){let p="";Array.from(h).forEach(f=>{p+=f.textContent,f.parentNode.removeChild(f)}),l._styleSheet=new CSSStyleSheet,l._styleSheet.replaceSync(p)}}}function n(l){let d=null;if(l&&(!Yt||Yi)&&(d=W.import(l,"template"),Yt&&!d))throw new Error(`strictTemplatePolicy: expecting dom-module or null template for ${l}`);return d}class a extends e{static get polymerElementVersion(){return cs}static _finalizeClass(){e._finalizeClass.call(this);const d=i(this);d&&this.createObservers(d,this._properties),this._prepareTemplate()}static _prepareTemplate(){let d=this.template;d&&(typeof d=="string"?(console.error("template getter must return HTMLTemplateElement"),d=null):Gi||(d=d.cloneNode(!0))),this.prototype._template=d}static createProperties(d){for(let c in d)r(this.prototype,c,d[c],d)}static createObservers(d,c){const u=this.prototype;for(let h=0;h<d.length;h++)u._createMethodObserver(d[h],c)}static get template(){if(!this.hasOwnProperty(JSCompiler_renameProperty("_template",this))){let d=this.prototype.hasOwnProperty(JSCompiler_renameProperty("_template",this.prototype))?this.prototype._template:void 0;typeof d=="function"&&(d=d()),this._template=d!==void 0?d:this.hasOwnProperty(JSCompiler_renameProperty("is",this))&&n(this.is)||Object.getPrototypeOf(this.prototype).constructor.template}return this._template}static set template(d){this._template=d}static get importPath(){if(!this.hasOwnProperty(JSCompiler_renameProperty("_importPath",this))){const d=this.importMeta;if(d)this._importPath=ae(d.url);else{const c=W.import(this.is);this._importPath=c&&c.assetpath||Object.getPrototypeOf(this.prototype).constructor.importPath}}return this._importPath}constructor(){super(),this._template,this._importPath,this.rootPath,this.importPath,this.root,this.$}_initializeProperties(){this.constructor.finalize(),this.constructor._finalizeTemplate(this.localName),super._initializeProperties(),this.rootPath=Wi,this.importPath=this.constructor.importPath;let d=t(this.constructor);if(d)for(let c in d){let u=d[c];if(this._canApplyPropertyDefault(c)){let h=typeof u.value=="function"?u.value.call(this):u.value;this._hasAccessor(c)?this._setPendingProperty(c,h,!0):this[c]=h}}}_canApplyPropertyDefault(d){return!this.hasOwnProperty(d)}static _processStyleText(d,c){return ne(d,c)}static _finalizeTemplate(d){const c=this.prototype._template;if(c&&!c.__polymerFinalized){c.__polymerFinalized=!0;const u=this.importPath,h=u?ot(u):"";s(this,c,d,h),this.prototype._bindTemplate(c)}}connectedCallback(){window.ShadyCSS&&this._template&&window.ShadyCSS.styleElement(this),super.connectedCallback()}ready(){this._template&&(this.root=this._stampTemplate(this._template),this.$=this.root.$),super.ready()}_readyClients(){this._template&&(this.root=this._attachDom(this.root)),super._readyClients()}_attachDom(d){const c=I(this);if(c.attachShadow)return d?(c.shadowRoot||(c.attachShadow({mode:"open",shadyUpgradeFragment:d}),c.shadowRoot.appendChild(d),this.constructor._styleSheet&&(c.shadowRoot.adoptedStyleSheets=[this.constructor._styleSheet])),Ki&&window.ShadyDOM&&window.ShadyDOM.flushInitial(c.shadowRoot),c.shadowRoot):null;throw new Error("ShadowDOM not available. PolymerElement can create dom as children instead of in ShadowDOM by setting `this.root = this;` before `ready`.")}updateStyles(d){window.ShadyCSS&&window.ShadyCSS.styleSubtree(this,d)}resolveUrl(d,c){return!c&&this.importPath&&(c=ot(this.importPath)),ot(d,c)}static _parseTemplateContent(d,c,u){return c.dynamicFns=c.dynamicFns||this._properties,e._parseTemplateContent.call(this,d,c,u)}static _addTemplatePropertyEffect(d,c,u){return $i&&!(c in this._properties)&&!(u.info.part.signature&&u.info.part.signature.static)&&!u.info.part.hostProp&&!d.nestedTemplate&&console.warn(`Property '${c}' used in template but not declared in 'properties'; attribute will not be observed.`),e._addTemplatePropertyEffect.call(this,d,c,u)}}return a});/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const Be=window.trustedTypes&&trustedTypes.createPolicy("polymer-html-literal",{createHTML:o=>o});class Po{constructor(e,t){So(e,t);const i=t.reduce((r,s,n)=>r+To(s)+e[n+1],e[0]);this.value=i.toString()}toString(){return this.value}}function To(o){if(o instanceof Po)return o.value;throw new Error(`non-literal value passed to Polymer's htmlLiteral function: ${o}`)}function hs(o){if(o instanceof HTMLTemplateElement)return o.innerHTML;if(o instanceof Po)return To(o);throw new Error(`non-template value passed to Polymer's html function: ${o}`)}const z=function(e,...t){So(e,t);const i=document.createElement("template");let r=t.reduce((s,n,a)=>s+hs(n)+e[a+1],e[0]);return Be&&(r=Be.createHTML(r)),i.innerHTML=r,i},So=(o,e)=>{if(!Array.isArray(o)||!Array.isArray(o.raw)||e.length!==o.length-1)throw new TypeError("Invalid call to the html template tag")};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const F=us(HTMLElement);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const T=[];function Zt(o,e,t=o.getAttribute("dir")){e?o.setAttribute("dir",e):t!=null&&o.removeAttribute("dir")}function Xt(){return document.documentElement.getAttribute("dir")}function ps(){const o=Xt();T.forEach(e=>{Zt(e,o)})}const fs=new MutationObserver(ps);fs.observe(document.documentElement,{attributes:!0,attributeFilter:["dir"]});const ue=o=>class extends o{static get properties(){return{dir:{type:String,value:"",reflectToAttribute:!0,converter:{fromAttribute:t=>t||"",toAttribute:t=>t===""?null:t}}}}get __isRTL(){return this.getAttribute("dir")==="rtl"}connectedCallback(){super.connectedCallback(),(!this.hasAttribute("dir")||this.__restoreSubscription)&&(this.__subscribe(),Zt(this,Xt(),null))}attributeChangedCallback(t,i,r){if(super.attributeChangedCallback(t,i,r),t!=="dir")return;const s=Xt(),n=r===s&&T.indexOf(this)===-1,a=!r&&i&&T.indexOf(this)===-1;n||a?(this.__subscribe(),Zt(this,s,r)):r!==s&&i===s&&this.__unsubscribe()}disconnectedCallback(){super.disconnectedCallback(),this.__restoreSubscription=T.includes(this),this.__unsubscribe()}_valueToNodeAttribute(t,i,r){r==="dir"&&i===""&&!t.hasAttribute("dir")||super._valueToNodeAttribute(t,i,r)}_attributeToProperty(t,i,r){t==="dir"&&!i?this.dir="":super._attributeToProperty(t,i,r)}__subscribe(){T.includes(this)||T.push(this)}__unsubscribe(){T.includes(this)&&T.splice(T.indexOf(this),1)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ms=_`
  :host {
    z-index: 200;
    position: fixed;

    /* Despite of what the names say, <vaadin-overlay> is just a container
          for position/sizing/alignment. The actual overlay is the overlay part. */

    /* Default position constraints: the entire viewport. Note: themes can
          override this to introduce gaps between the overlay and the viewport. */
    inset: 0;
    bottom: var(--vaadin-overlay-viewport-bottom);

    /* Use flexbox alignment for the overlay part. */
    display: flex;
    flex-direction: column; /* makes dropdowns sizing easier */
    /* Align to center by default. */
    align-items: center;
    justify-content: center;

    /* Allow centering when max-width/max-height applies. */
    margin: auto;

    /* The host is not clickable, only the overlay part is. */
    pointer-events: none;

    /* Remove tap highlight on touch devices. */
    -webkit-tap-highlight-color: transparent;

    /* CSS API for host */
    --vaadin-overlay-viewport-bottom: 0;
  }

  :host([hidden]),
  :host(:not([opened]):not([closing])) {
    display: none !important;
  }

  [part='overlay'] {
    -webkit-overflow-scrolling: touch;
    overflow: auto;
    pointer-events: auto;

    /* Prevent overflowing the host */
    max-width: 100%;
    box-sizing: border-box;

    -webkit-tap-highlight-color: initial; /* reenable tap highlight inside */
  }

  [part='backdrop'] {
    z-index: -1;
    content: '';
    background: rgba(0, 0, 0, 0.5);
    position: fixed;
    inset: 0;
    pointer-events: auto;
  }
`;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let wt=!1,ko=[],Oo=[];function zo(){wt=!0,requestAnimationFrame(function(){wt=!1,_s(ko),setTimeout(function(){gs(Oo)})})}function _s(o){for(;o.length;)Mo(o.shift())}function gs(o){for(let e=0,t=o.length;e<t;e++)Mo(o.shift())}function Mo(o){const e=o[0],t=o[1],i=o[2];try{t.apply(e,i)}catch(r){setTimeout(()=>{throw r})}}function Da(o,e,t){wt||zo(),ko.push([o,e,t])}function vs(o,e,t){wt||zo(),Oo.push([o,e,t])}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Pt=o=>o.test(navigator.userAgent),Qt=o=>o.test(navigator.platform),bs=o=>o.test(navigator.vendor),Fa=Pt(/Android/u),Ra=Pt(/Chrome/u)&&bs(/Google Inc/u),Ha=Pt(/Firefox/u),ys=Qt(/^iPad/u)||Qt(/^Mac/u)&&navigator.maxTouchPoints>1,xs=Qt(/^iPhone/u),ws=xs||ys,Ba=Pt(/^((?!chrome|android).)*safari/iu),Cs=(()=>{try{return document.createEvent("TouchEvent"),!0}catch{return!1}})();/**
 * @license
 * Copyright (c) 2017 Anton Korzunov
 * SPDX-License-Identifier: MIT
 */let H=new WeakMap,pt=new WeakMap,ft={},Nt=0;const Ve=o=>o&&o.nodeType===Node.ELEMENT_NODE,It=(...o)=>{console.error(`Error: ${o.join(" ")}. Skip setting aria-hidden.`)},As=(o,e)=>Ve(o)?e.map(t=>{if(!Ve(t))return It(t,"is not a valid element"),null;let i=t;for(;i&&i!==o;){if(o.contains(i))return t;i=i.getRootNode().host}return It(t,"is not contained inside",o),null}).filter(t=>!!t):(It(o,"is not a valid element"),[]),Es=(o,e,t,i)=>{const r=As(e,Array.isArray(o)?o:[o]);ft[t]||(ft[t]=new WeakMap);const s=ft[t],n=[],a=new Set,l=new Set(r),d=u=>{if(!u||a.has(u))return;a.add(u);const h=u.assignedSlot;h&&d(h),d(u.parentNode||u.host)};r.forEach(d);const c=u=>{if(!u||l.has(u))return;const h=u.shadowRoot;(h?[...u.children,...h.children]:[...u.children]).forEach(f=>{if(!["template","script","style"].includes(f.localName))if(a.has(f))c(f);else{const x=f.getAttribute(i),w=x!==null&&x!=="false",b=(H.get(f)||0)+1,y=(s.get(f)||0)+1;H.set(f,b),s.set(f,y),n.push(f),b===1&&w&&pt.set(f,!0),y===1&&f.setAttribute(t,"true"),w||f.setAttribute(i,"true")}})};return c(e),a.clear(),Nt+=1,()=>{n.forEach(u=>{const h=H.get(u)-1,p=s.get(u)-1;H.set(u,h),s.set(u,p),h||(pt.has(u)?pt.delete(u):u.removeAttribute(i)),p||u.removeAttribute(t)}),Nt-=1,Nt||(H=new WeakMap,H=new WeakMap,pt=new WeakMap,ft={})}},Ps=(o,e=document.body,t="data-aria-hidden")=>{const i=Array.from(Array.isArray(o)?o:[o]);return e&&i.push(...Array.from(e.querySelectorAll("[aria-live]"))),Es(i,e,t,"aria-hidden")};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Ts{constructor(e,t){this.host=e,this.callback=typeof t=="function"?t:()=>e}showModal(){const e=this.callback();this.__showOthers=Ps(e)}close(){this.__showOthers&&(this.__showOthers(),this.__showOthers=null)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */let he=!1;window.addEventListener("keydown",()=>{he=!0},{capture:!0});window.addEventListener("mousedown",()=>{he=!1},{capture:!0});function te(){let o=document.activeElement||document.body;for(;o.shadowRoot&&o.shadowRoot.activeElement;)o=o.shadowRoot.activeElement;return o}function No(){return he}function Io(o){const e=o.style;if(e.visibility==="hidden"||e.display==="none")return!0;const t=window.getComputedStyle(o);return t.visibility==="hidden"||t.display==="none"}function Ss(o,e){const t=Math.max(o.tabIndex,0),i=Math.max(e.tabIndex,0);return t===0||i===0?i>t:t>i}function ks(o,e){const t=[];for(;o.length>0&&e.length>0;)Ss(o[0],e[0])?t.push(e.shift()):t.push(o.shift());return t.concat(o,e)}function ee(o){const e=o.length;if(e<2)return o;const t=Math.ceil(e/2),i=ee(o.slice(0,t)),r=ee(o.slice(t));return ks(i,r)}function Va(o){return o.offsetParent===null&&o.clientWidth===0&&o.clientHeight===0?!0:Io(o)}function Os(o){return o.matches('[tabindex="-1"]')?!1:o.matches("input, select, textarea, button, object")?o.matches(":not([disabled])"):o.matches("a[href], area[href], iframe, [tabindex], [contentEditable]")}function zs(o){return o.getRootNode().activeElement===o}function Ms(o){if(!Os(o))return-1;const e=o.getAttribute("tabindex")||0;return Number(e)}function Lo(o,e){if(o.nodeType!==Node.ELEMENT_NODE||Io(o))return!1;const t=o,i=Ms(t);let r=i>0;i>=0&&e.push(t);let s=[];return t.localName==="slot"?s=t.assignedNodes({flatten:!0}):s=(t.shadowRoot||t).children,[...s].forEach(n=>{r=Lo(n,e)||r}),r}function Ns(o){const e=[];return Lo(o,e)?ee(e):e}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Is{saveFocus(e){this.focusNode=e||te()}restoreFocus(){const e=this.focusNode;e&&(te()===document.body?setTimeout(()=>e.focus()):e.focus(),this.focusNode=null)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Lt=[];class Ls{constructor(e){this.host=e,this.__trapNode=null,this.__onKeyDown=this.__onKeyDown.bind(this)}get __focusableElements(){return Ns(this.__trapNode)}get __focusedElementIndex(){const e=this.__focusableElements;return e.indexOf(e.filter(zs).pop())}hostConnected(){document.addEventListener("keydown",this.__onKeyDown)}hostDisconnected(){document.removeEventListener("keydown",this.__onKeyDown)}trapFocus(e){if(this.__trapNode=e,this.__focusableElements.length===0)throw this.__trapNode=null,new Error("The trap node should have at least one focusable descendant or be focusable itself.");Lt.push(this),this.__focusedElementIndex===-1&&this.__focusableElements[0].focus()}releaseFocus(){this.__trapNode=null,Lt.pop()}__onKeyDown(e){if(this.__trapNode&&this===Array.from(Lt).pop()&&e.key==="Tab"){e.preventDefault();const t=e.shiftKey;this.__focusNextElement(t)}}__focusNextElement(e=!1){const t=this.__focusableElements,i=e?-1:1,r=this.__focusedElementIndex,s=(t.length+r+i)%t.length,n=t[s];n.focus(),n.localName==="input"&&n.select()}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const at=v(o=>typeof o.prototype.addController=="function"?o:class extends o{constructor(){super(),this.__controllers=new Set}connectedCallback(){super.connectedCallback(),this.__controllers.forEach(t=>{t.hostConnected&&t.hostConnected()})}disconnectedCallback(){super.disconnectedCallback(),this.__controllers.forEach(t=>{t.hostDisconnected&&t.hostDisconnected()})}addController(t){this.__controllers.add(t),this.$!==void 0&&this.isConnected&&t.hostConnected&&t.hostConnected()}removeController(t){this.__controllers.delete(t)}});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ds=o=>class extends at(o){static get properties(){return{focusTrap:{type:Boolean,value:!1},restoreFocusOnClose:{type:Boolean,value:!1},restoreFocusNode:{type:HTMLElement}}}constructor(){super(),this.__ariaModalController=new Ts(this),this.__focusTrapController=new Ls(this),this.__focusRestorationController=new Is}ready(){super.ready(),this.addController(this.__ariaModalController),this.addController(this.__focusTrapController),this.addController(this.__focusRestorationController)}_resetFocus(){this.focusTrap&&(this.__ariaModalController.close(),this.__focusTrapController.releaseFocus()),this.restoreFocusOnClose&&this._shouldRestoreFocus()&&this.__focusRestorationController.restoreFocus()}_saveFocus(){this.restoreFocusOnClose&&this.__focusRestorationController.saveFocus(this.restoreFocusNode)}_trapFocus(){this.focusTrap&&(this.__ariaModalController.showModal(),this.__focusTrapController.trapFocus(this.$.overlay))}_shouldRestoreFocus(){const t=te();return t===document.body||this._deepContains(t)}_deepContains(t){if(this.contains(t))return!0;let i=t;const r=t.ownerDocument;for(;i&&i!==r&&i!==this;)i=i.parentNode||i.host;return i===this}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const _t=()=>Array.from(document.body.children).filter(o=>o instanceof HTMLElement&&o._hasOverlayStackMixin&&!o.hasAttribute("closing")).sort((o,e)=>o.__zIndex-e.__zIndex||0),Fs=o=>o===_t().pop(),Rs=o=>class extends o{constructor(){super(),this._hasOverlayStackMixin=!0}get _last(){return Fs(this)}bringToFront(){let t="";const i=_t().filter(r=>r!==this).pop();i&&(t=i.__zIndex+1),this.style.zIndex=t,this.__zIndex=t||parseFloat(getComputedStyle(this).zIndex)}_enterModalState(){document.body.style.pointerEvents!=="none"&&(this._previousDocumentPointerEvents=document.body.style.pointerEvents,document.body.style.pointerEvents="none"),_t().forEach(t=>{t!==this&&(t.$.overlay.style.pointerEvents="none")})}_exitModalState(){this._previousDocumentPointerEvents!==void 0&&(document.body.style.pointerEvents=this._previousDocumentPointerEvents,delete this._previousDocumentPointerEvents);const t=_t();let i;for(;(i=t.pop())&&!(i!==this&&(i.$.overlay.style.removeProperty("pointer-events"),!i.modeless)););}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Hs=o=>class extends Ds(Rs(o)){static get properties(){return{opened:{type:Boolean,notify:!0,observer:"_openedChanged",reflectToAttribute:!0},owner:{type:Object},model:{type:Object},renderer:{type:Object},modeless:{type:Boolean,value:!1,reflectToAttribute:!0,observer:"_modelessChanged"},hidden:{type:Boolean,reflectToAttribute:!0,observer:"_hiddenChanged"},withBackdrop:{type:Boolean,value:!1,reflectToAttribute:!0}}}static get observers(){return["_rendererOrDataChanged(renderer, owner, model, opened)"]}constructor(){super(),this._boundMouseDownListener=this._mouseDownListener.bind(this),this._boundMouseUpListener=this._mouseUpListener.bind(this),this._boundOutsideClickListener=this._outsideClickListener.bind(this),this._boundKeydownListener=this._keydownListener.bind(this),ws&&(this._boundIosResizeListener=()=>this._detectIosNavbar())}ready(){super.ready(),this.addEventListener("click",()=>{}),this.$.backdrop.addEventListener("click",()=>{}),this.addEventListener("mouseup",()=>{document.activeElement===document.body&&this.$.overlay.getAttribute("tabindex")==="0"&&this.$.overlay.focus()})}connectedCallback(){super.connectedCallback(),this._boundIosResizeListener&&(this._detectIosNavbar(),window.addEventListener("resize",this._boundIosResizeListener))}disconnectedCallback(){super.disconnectedCallback(),this._boundIosResizeListener&&window.removeEventListener("resize",this._boundIosResizeListener)}requestContentUpdate(){this.renderer&&this.renderer.call(this.owner,this,this.owner,this.model)}close(t){const i=new CustomEvent("vaadin-overlay-close",{bubbles:!0,cancelable:!0,detail:{sourceEvent:t}});this.dispatchEvent(i),i.defaultPrevented||(this.opened=!1)}_detectIosNavbar(){if(!this.opened)return;const t=window.innerHeight,r=window.innerWidth>t,s=document.documentElement.clientHeight;r&&s>t?this.style.setProperty("--vaadin-overlay-viewport-bottom",`${s-t}px`):this.style.setProperty("--vaadin-overlay-viewport-bottom","0")}_addGlobalListeners(){document.addEventListener("mousedown",this._boundMouseDownListener),document.addEventListener("mouseup",this._boundMouseUpListener),document.documentElement.addEventListener("click",this._boundOutsideClickListener,!0)}_removeGlobalListeners(){document.removeEventListener("mousedown",this._boundMouseDownListener),document.removeEventListener("mouseup",this._boundMouseUpListener),document.documentElement.removeEventListener("click",this._boundOutsideClickListener,!0)}_rendererOrDataChanged(t,i,r,s){const n=this._oldOwner!==i||this._oldModel!==r;this._oldModel=r,this._oldOwner=i;const a=this._oldRenderer!==t;this._oldRenderer=t;const l=this._oldOpened!==s;this._oldOpened=s,a&&(this.innerHTML="",delete this._$litPart$),s&&t&&(a||l||n)&&this.requestContentUpdate()}_modelessChanged(t){t?(this._removeGlobalListeners(),this._exitModalState()):this.opened&&(this._addGlobalListeners(),this._enterModalState())}_openedChanged(t,i){t?(this._saveFocus(),this._animatedOpening(),vs(this,()=>{this._trapFocus();const r=new CustomEvent("vaadin-overlay-open",{bubbles:!0});this.dispatchEvent(r)}),document.addEventListener("keydown",this._boundKeydownListener),this.modeless||this._addGlobalListeners()):i&&(this._resetFocus(),this._animatedClosing(),document.removeEventListener("keydown",this._boundKeydownListener),this.modeless||this._removeGlobalListeners())}_hiddenChanged(t){t&&this.hasAttribute("closing")&&this._flushAnimation("closing")}_shouldAnimate(){const t=getComputedStyle(this),i=t.getPropertyValue("animation-name");return!(t.getPropertyValue("display")==="none")&&i&&i!=="none"}_enqueueAnimation(t,i){const r=`__${t}Handler`,s=n=>{n&&n.target!==this||(i(),this.removeEventListener("animationend",s),delete this[r])};this[r]=s,this.addEventListener("animationend",s)}_flushAnimation(t){const i=`__${t}Handler`;typeof this[i]=="function"&&this[i]()}_animatedOpening(){this.parentNode===document.body&&this.hasAttribute("closing")&&this._flushAnimation("closing"),this._attachOverlay(),this.modeless||this._enterModalState(),this.setAttribute("opening",""),this._shouldAnimate()?this._enqueueAnimation("opening",()=>{this._finishOpening()}):this._finishOpening()}_attachOverlay(){this._placeholder=document.createComment("vaadin-overlay-placeholder"),this.parentNode.insertBefore(this._placeholder,this),document.body.appendChild(this),this.bringToFront()}_finishOpening(){this.removeAttribute("opening")}_finishClosing(){this._detachOverlay(),this.$.overlay.style.removeProperty("pointer-events"),this.removeAttribute("closing"),this.dispatchEvent(new CustomEvent("vaadin-overlay-closed"))}_animatedClosing(){this.hasAttribute("opening")&&this._flushAnimation("opening"),this._placeholder&&(this._exitModalState(),this.setAttribute("closing",""),this.dispatchEvent(new CustomEvent("vaadin-overlay-closing")),this._shouldAnimate()?this._enqueueAnimation("closing",()=>{this._finishClosing()}):this._finishClosing())}_detachOverlay(){this._placeholder.parentNode.insertBefore(this,this._placeholder),this._placeholder.parentNode.removeChild(this._placeholder)}_mouseDownListener(t){this._mouseDownInside=t.composedPath().indexOf(this.$.overlay)>=0}_mouseUpListener(t){this._mouseUpInside=t.composedPath().indexOf(this.$.overlay)>=0}_shouldCloseOnOutsideClick(t){return this._last}_outsideClickListener(t){if(t.composedPath().includes(this.$.overlay)||this._mouseDownInside||this._mouseUpInside){this._mouseDownInside=!1,this._mouseUpInside=!1;return}if(!this._shouldCloseOnOutsideClick(t))return;const i=new CustomEvent("vaadin-overlay-outside-click",{bubbles:!0,cancelable:!0,detail:{sourceEvent:t}});this.dispatchEvent(i),this.opened&&!i.defaultPrevented&&this.close(t)}_keydownListener(t){if(this._last&&!(this.modeless&&!t.composedPath().includes(this.$.overlay))&&t.key==="Escape"){const i=new CustomEvent("vaadin-overlay-escape-press",{bubbles:!0,cancelable:!0,detail:{sourceEvent:t}});this.dispatchEvent(i),this.opened&&!i.defaultPrevented&&this.close(t)}}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Bs(o){const e=[];for(;o;){if(o.nodeType===Node.DOCUMENT_NODE){e.push(o);break}if(o.nodeType===Node.DOCUMENT_FRAGMENT_NODE){e.push(o),o=o.host;continue}if(o.assignedSlot){o=o.assignedSlot;continue}o=o.parentNode}return e}function Vs(o){const e=[];let t;return o.localName==="slot"?t=o.assignedElements():(e.push(o),t=[...o.children]),t.forEach(i=>e.push(...Vs(i))),e}function Us(o,e){return e?e.closest(o)||Us(o,e.getRootNode().host):null}function pe(o){return o?new Set(o.split(" ")):new Set}function Tt(o){return o?[...o].join(" "):""}function fe(o,e,t){const i=pe(o.getAttribute(e));i.add(t),o.setAttribute(e,Tt(i))}function Do(o,e,t){const i=pe(o.getAttribute(e));if(i.delete(t),i.size===0){o.removeAttribute(e);return}o.setAttribute(e,Tt(i))}function js(o){return o.nodeType===Node.TEXT_NODE&&o.textContent.trim()===""}/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Dt={start:"top",end:"bottom"},Ft={start:"left",end:"right"},Ue=new ResizeObserver(o=>{setTimeout(()=>{o.forEach(e=>{e.target.__overlay&&e.target.__overlay._updatePosition()})})}),qs=o=>class extends o{static get properties(){return{positionTarget:{type:Object,value:null,sync:!0},horizontalAlign:{type:String,value:"start",sync:!0},verticalAlign:{type:String,value:"top",sync:!0},noHorizontalOverlap:{type:Boolean,value:!1,sync:!0},noVerticalOverlap:{type:Boolean,value:!1,sync:!0},requiredVerticalSpace:{type:Number,value:0,sync:!0}}}static get observers(){return["__positionSettingsChanged(horizontalAlign, verticalAlign, noHorizontalOverlap, noVerticalOverlap, requiredVerticalSpace)","__overlayOpenedChanged(opened, positionTarget)"]}constructor(){super(),this.__onScroll=this.__onScroll.bind(this),this._updatePosition=this._updatePosition.bind(this)}connectedCallback(){super.connectedCallback(),this.opened&&this.__addUpdatePositionEventListeners()}disconnectedCallback(){super.disconnectedCallback(),this.__removeUpdatePositionEventListeners()}__addUpdatePositionEventListeners(){window.addEventListener("resize",this._updatePosition),this.__positionTargetAncestorRootNodes=Bs(this.positionTarget),this.__positionTargetAncestorRootNodes.forEach(t=>{t.addEventListener("scroll",this.__onScroll,!0)})}__removeUpdatePositionEventListeners(){window.removeEventListener("resize",this._updatePosition),this.__positionTargetAncestorRootNodes&&(this.__positionTargetAncestorRootNodes.forEach(t=>{t.removeEventListener("scroll",this.__onScroll,!0)}),this.__positionTargetAncestorRootNodes=null)}__overlayOpenedChanged(t,i){if(this.__removeUpdatePositionEventListeners(),i&&(i.__overlay=null,Ue.unobserve(i),t&&(this.__addUpdatePositionEventListeners(),i.__overlay=this,Ue.observe(i))),t){const r=getComputedStyle(this);this.__margins||(this.__margins={},["top","bottom","left","right"].forEach(s=>{this.__margins[s]=parseInt(r[s],10)})),this.setAttribute("dir",r.direction),this._updatePosition(),requestAnimationFrame(()=>this._updatePosition())}}__positionSettingsChanged(){this._updatePosition()}__onScroll(t){this.contains(t.target)||this._updatePosition()}_updatePosition(){if(!this.positionTarget||!this.opened)return;const t=this.positionTarget.getBoundingClientRect(),i=this.__shouldAlignStartVertically(t);this.style.justifyContent=i?"flex-start":"flex-end";const r=this.__isRTL,s=this.__shouldAlignStartHorizontally(t,r),n=!r&&s||r&&!s;this.style.alignItems=n?"flex-start":"flex-end";const a=this.getBoundingClientRect(),l=this.__calculatePositionInOneDimension(t,a,this.noVerticalOverlap,Dt,this,i),d=this.__calculatePositionInOneDimension(t,a,this.noHorizontalOverlap,Ft,this,s);Object.assign(this.style,l,d),this.toggleAttribute("bottom-aligned",!i),this.toggleAttribute("top-aligned",i),this.toggleAttribute("end-aligned",!n),this.toggleAttribute("start-aligned",n)}__shouldAlignStartHorizontally(t,i){const r=Math.max(this.__oldContentWidth||0,this.$.overlay.offsetWidth);this.__oldContentWidth=this.$.overlay.offsetWidth;const s=Math.min(window.innerWidth,document.documentElement.clientWidth),n=!i&&this.horizontalAlign==="start"||i&&this.horizontalAlign==="end";return this.__shouldAlignStart(t,r,s,this.__margins,n,this.noHorizontalOverlap,Ft)}__shouldAlignStartVertically(t){const i=this.requiredVerticalSpace||Math.max(this.__oldContentHeight||0,this.$.overlay.offsetHeight);this.__oldContentHeight=this.$.overlay.offsetHeight;const r=Math.min(window.innerHeight,document.documentElement.clientHeight),s=this.verticalAlign==="top";return this.__shouldAlignStart(t,i,r,this.__margins,s,this.noVerticalOverlap,Dt)}__shouldAlignStart(t,i,r,s,n,a,l){const d=r-t[a?l.end:l.start]-s[l.end],c=t[a?l.start:l.end]-s[l.start],u=n?d:c,p=u>(n?c:d)||u>i;return n===p}__adjustBottomProperty(t,i,r){let s;if(t===i.end){if(i.end===Dt.end){const n=Math.min(window.innerHeight,document.documentElement.clientHeight);if(r>n&&this.__oldViewportHeight){const a=this.__oldViewportHeight-n;s=r-a}this.__oldViewportHeight=n}if(i.end===Ft.end){const n=Math.min(window.innerWidth,document.documentElement.clientWidth);if(r>n&&this.__oldViewportWidth){const a=this.__oldViewportWidth-n;s=r-a}this.__oldViewportWidth=n}}return s}__calculatePositionInOneDimension(t,i,r,s,n,a){const l=a?s.start:s.end,d=a?s.end:s.start,c=parseFloat(n.style[l]||getComputedStyle(n)[l]),u=this.__adjustBottomProperty(l,s,c),h=i[a?s.start:s.end]-t[r===a?s.end:s.start],p=u?`${u}px`:`${c+h*(a?-1:1)}px`;return{[l]:p,[d]:""}}};/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ws=o=>class extends qs(Hs(o)){static get properties(){return{position:{type:String,reflectToAttribute:!0}}}requestContentUpdate(){if(super.requestContentUpdate(),this.toggleAttribute("hidden",this.textContent.trim()===""),this.positionTarget&&this.owner){const t=getComputedStyle(this.owner);["top","bottom","start","end"].forEach(i=>{this.style.setProperty(`--vaadin-tooltip-offset-${i}`,t.getPropertyValue(`--vaadin-tooltip-offset-${i}`))})}}_updatePosition(){if(super._updatePosition(),!!this.positionTarget){if(this.position==="bottom"||this.position==="top"){const t=this.positionTarget.getBoundingClientRect(),i=this.$.overlay.getBoundingClientRect(),r=t.width/2-i.width/2;if(this.style.left){const s=i.left+r;s>0&&(this.style.left=`${s}px`)}if(this.style.right){const s=parseFloat(this.style.right)+r;s>0&&(this.style.right=`${s}px`)}}if(this.position==="start"||this.position==="end"){const t=this.positionTarget.getBoundingClientRect(),i=this.$.overlay.getBoundingClientRect(),r=t.height/2-i.height/2;this.style.top=`${i.top+r}px`}}}};/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ys=_`
  :host {
    z-index: 1100;
  }

  [part='overlay'] {
    max-width: 40ch;
  }

  :host([position^='top'][top-aligned]) [part='overlay'],
  :host([position^='bottom'][top-aligned]) [part='overlay'] {
    margin-top: var(--vaadin-tooltip-offset-top, 0);
  }

  :host([position^='top'][bottom-aligned]) [part='overlay'],
  :host([position^='bottom'][bottom-aligned]) [part='overlay'] {
    margin-bottom: var(--vaadin-tooltip-offset-bottom, 0);
  }

  :host([position^='start'][start-aligned]) [part='overlay'],
  :host([position^='end'][start-aligned]) [part='overlay'] {
    margin-inline-start: var(--vaadin-tooltip-offset-start, 0);
  }

  :host([position^='start'][end-aligned]) [part='overlay'],
  :host([position^='end'][end-aligned]) [part='overlay'] {
    margin-inline-end: var(--vaadin-tooltip-offset-end, 0);
  }

  @media (forced-colors: active) {
    [part='overlay'] {
      outline: 1px dashed;
    }
  }
`;/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-tooltip-overlay",[ms,Ys],{moduleId:"vaadin-tooltip-overlay-styles"});class Gs extends Ws(ue(nt(F))){static get is(){return"vaadin-tooltip-overlay"}static get template(){return z`
      <div id="backdrop" part="backdrop" hidden></div>
      <div part="overlay" id="overlay">
        <div part="content" id="content"><slot></slot></div>
      </div>
    `}ready(){super.ready(),this.owner=this.__dataHost,this.owner._overlayElement=this}}P(Gs);const $s=/\/\*[\*!]\s+vaadin-dev-mode:start([\s\S]*)vaadin-dev-mode:end\s+\*\*\//i,gt=window.Vaadin&&window.Vaadin.Flow&&window.Vaadin.Flow.clients;function Ks(){function o(){return!0}return Fo(o)}function Js(){try{return Zs()?!0:Xs()?gt?!Qs():!Ks():!1}catch{return!1}}function Zs(){return localStorage.getItem("vaadin.developmentmode.force")}function Xs(){return["localhost","127.0.0.1"].indexOf(window.location.hostname)>=0}function Qs(){return!!(gt&&Object.keys(gt).map(e=>gt[e]).filter(e=>e.productionMode).length>0)}function Fo(o,e){if(typeof o!="function")return;const t=$s.exec(o.toString());if(t)try{o=new Function(t[1])}catch(i){console.log("vaadin-development-mode-detector: uncommentAndRun() failed",i)}return o(e)}window.Vaadin=window.Vaadin||{};const je=function(o,e){if(window.Vaadin.developmentMode)return Fo(o,e)};window.Vaadin.developmentMode===void 0&&(window.Vaadin.developmentMode=Js());function tn(){}const en=function(){if(typeof je=="function")return je(tn)};/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
 */let qe=0,Ro=0;const U=[];let oe=!1;function on(){oe=!1;const o=U.length;for(let e=0;e<o;e++){const t=U[e];if(t)try{t()}catch(i){setTimeout(()=>{throw i})}}U.splice(0,o),Ro+=o}const rn={after(o){return{run(e){return window.setTimeout(e,o)},cancel(e){window.clearTimeout(e)}}},run(o,e){return window.setTimeout(o,e)},cancel(o){window.clearTimeout(o)}},$a={run(o){return window.requestAnimationFrame(o)},cancel(o){window.cancelAnimationFrame(o)}},sn={run(o){return window.requestIdleCallback?window.requestIdleCallback(o):window.setTimeout(o,16)},cancel(o){window.cancelIdleCallback?window.cancelIdleCallback(o):window.clearTimeout(o)}},Ho={run(o){oe||(oe=!0,queueMicrotask(()=>on())),U.push(o);const e=qe;return qe+=1,e},cancel(o){const e=o-Ro;if(e>=0){if(!U[e])throw new Error(`invalid async handle: ${o}`);U[e]=null}}};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const st=new Set;class Y{static debounce(e,t,i){return e instanceof Y?e._cancelAsync():e=new Y,e.setConfig(t,i),e}constructor(){this._asyncModule=null,this._callback=null,this._timer=null}setConfig(e,t){this._asyncModule=e,this._callback=t,this._timer=this._asyncModule.run(()=>{this._timer=null,st.delete(this),this._callback()})}cancel(){this.isActive()&&(this._cancelAsync(),st.delete(this))}_cancelAsync(){this.isActive()&&(this._asyncModule.cancel(this._timer),this._timer=null)}flush(){this.isActive()&&(this.cancel(),this._callback())}isActive(){return this._timer!=null}}function nn(o){st.add(o)}function an(){const o=!!st.size;return st.forEach(e=>{try{e.flush()}catch(t){setTimeout(()=>{throw t})}}),o}const Ka=()=>{let o;do o=an();while(o)};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */window.Vaadin||(window.Vaadin={});window.Vaadin.registrations||(window.Vaadin.registrations=[]);window.Vaadin.developmentModeCallback||(window.Vaadin.developmentModeCallback={});window.Vaadin.developmentModeCallback["vaadin-usage-statistics"]=function(){en()};let Rt;const We=new Set,lt=o=>class extends ue(o){static finalize(){super.finalize();const{is:t}=this;t&&!We.has(t)&&(window.Vaadin.registrations.push(this),We.add(t),window.Vaadin.developmentModeCallback&&(Rt=Y.debounce(Rt,sn,()=>{window.Vaadin.developmentModeCallback["vaadin-usage-statistics"]()}),nn(Rt)))}constructor(){super(),document.doctype===null&&console.warn('Vaadin components require the "standards mode" declaration. Please add <!DOCTYPE html> to the HTML document.')}};/**
 * @license
 * Copyright (c) 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ln=o=>class extends o{static get properties(){return{overlayClass:{type:String},_overlayElement:{type:Object}}}static get observers(){return["__updateOverlayClassNames(overlayClass, _overlayElement)"]}__updateOverlayClassNames(t,i){if(!i||t===void 0)return;const{classList:r}=i;if(this.__initialClasses||(this.__initialClasses=new Set(r)),Array.isArray(this.__previousClasses)){const n=this.__previousClasses.filter(a=>!this.__initialClasses.has(a));n.length>0&&r.remove(...n)}const s=typeof t=="string"?t.split(" "):[];s.length>0&&r.add(...s),this.__previousClasses=s}};/**
 * @license
 * Copyright (c) 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class dn{constructor(e,t){this.slot=e,this.callback=t,this._storedNodes=[],this._connected=!1,this._scheduled=!1,this._boundSchedule=()=>{this._schedule()},this.connect(),this._schedule()}connect(){this.slot.addEventListener("slotchange",this._boundSchedule),this._connected=!0}disconnect(){this.slot.removeEventListener("slotchange",this._boundSchedule),this._connected=!1}_schedule(){this._scheduled||(this._scheduled=!0,queueMicrotask(()=>{this.flush()}))}flush(){this._connected&&(this._scheduled=!1,this._processNodes())}_processNodes(){const e=this.slot.assignedNodes({flatten:!0});let t=[];const i=[],r=[];e.length&&(t=e.filter(s=>!this._storedNodes.includes(s))),this._storedNodes.length&&this._storedNodes.forEach((s,n)=>{const a=e.indexOf(s);a===-1?i.push(s):a!==n&&r.push(s)}),(t.length||i.length||r.length)&&this.callback({addedNodes:t,movedNodes:r,removedNodes:i}),this._storedNodes=e}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */let cn=0;function Bo(){return cn++}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class dt extends EventTarget{static generateId(e,t){return`${t||"default"}-${e.localName}-${Bo()}`}constructor(e,t,i,r={}){super();const{initializer:s,multiple:n,observe:a,useUniqueId:l}=r;this.host=e,this.slotName=t,this.tagName=i,this.observe=typeof a=="boolean"?a:!0,this.multiple=typeof n=="boolean"?n:!1,this.slotInitializer=s,n&&(this.nodes=[]),l&&(this.defaultId=this.constructor.generateId(e,t))}hostConnected(){this.initialized||(this.multiple?this.initMultiple():this.initSingle(),this.observe&&this.observeSlot(),this.initialized=!0)}initSingle(){let e=this.getSlotChild();e?(this.node=e,this.initAddedNode(e)):(e=this.attachDefaultNode(),this.initNode(e))}initMultiple(){const e=this.getSlotChildren();if(e.length===0){const t=this.attachDefaultNode();t&&(this.nodes=[t],this.initNode(t))}else this.nodes=e,e.forEach(t=>{this.initAddedNode(t)})}attachDefaultNode(){const{host:e,slotName:t,tagName:i}=this;let r=this.defaultNode;return!r&&i&&(r=document.createElement(i),r instanceof Element&&(t!==""&&r.setAttribute("slot",t),this.node=r,this.defaultNode=r)),r&&e.appendChild(r),r}getSlotChildren(){const{slotName:e}=this;return Array.from(this.host.childNodes).filter(t=>t.nodeType===Node.ELEMENT_NODE&&t.slot===e||t.nodeType===Node.TEXT_NODE&&t.textContent.trim()&&e==="")}getSlotChild(){return this.getSlotChildren()[0]}initNode(e){const{slotInitializer:t}=this;t&&t(e,this.host)}initCustomNode(e){}teardownNode(e){}initAddedNode(e){e!==this.defaultNode&&(this.initCustomNode(e),this.initNode(e))}observeSlot(){const{slotName:e}=this,t=e===""?"slot:not([name])":`slot[name=${e}]`,i=this.host.shadowRoot.querySelector(t);this.__slotObserver=new dn(i,({addedNodes:r,removedNodes:s})=>{const n=this.multiple?this.nodes:[this.node],a=r.filter(l=>!js(l)&&!n.includes(l));s.length&&(this.nodes=n.filter(l=>!s.includes(l)),s.forEach(l=>{this.teardownNode(l)})),a&&a.length>0&&(this.multiple?(this.defaultNode&&this.defaultNode.remove(),this.nodes=[...n,...a].filter(l=>l!==this.defaultNode),a.forEach(l=>{this.initAddedNode(l)})):(this.node&&this.node.remove(),this.node=a[0],this.initAddedNode(this.node)))})}}/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const j=500;let Vo=j,Uo=j,jo=j;const K=new Set;let J=!1,Z=null,X=null;class un{constructor(e){this.host=e}get openedProp(){return this.host.manual?"opened":"_autoOpened"}get focusDelay(){const e=this.host;return e.focusDelay!=null&&e.focusDelay>0?e.focusDelay:Vo}get hoverDelay(){const e=this.host;return e.hoverDelay!=null&&e.hoverDelay>0?e.hoverDelay:Uo}get hideDelay(){const e=this.host;return e.hideDelay!=null&&e.hideDelay>0?e.hideDelay:jo}get isClosing(){return K.has(this.host)}open(e={immediate:!1}){const{immediate:t,hover:i,focus:r}=e,s=i&&this.hoverDelay>0,n=r&&this.focusDelay>0;!t&&(s||n)&&!this.__closeTimeout?this.__warmupTooltip(n):this.__showTooltip()}close(e){!e&&this.hideDelay>0?this.__scheduleClose():(this.__abortClose(),this._setOpened(!1)),this.__abortWarmUp(),J&&(this.__abortCooldown(),this.__scheduleCooldown())}_isOpened(){return this.host[this.openedProp]}_setOpened(e){this.host[this.openedProp]=e}__flushClosingTooltips(){K.forEach(e=>{e._stateController.close(!0),K.delete(e)})}__showTooltip(){this.__abortClose(),this.__flushClosingTooltips(),this._setOpened(!0),J=!0,this.__abortWarmUp(),this.__abortCooldown()}__warmupTooltip(e){this._isOpened()||(J?this.__showTooltip():this.__scheduleWarmUp(e))}__abortClose(){this.__closeTimeout&&(clearTimeout(this.__closeTimeout),this.__closeTimeout=null)}__abortCooldown(){X&&(clearTimeout(X),X=null)}__abortWarmUp(){Z&&(clearTimeout(Z),Z=null)}__scheduleClose(){this._isOpened()&&(K.add(this.host),this.__closeTimeout=setTimeout(()=>{K.delete(this.host),this.__closeTimeout=null,this._setOpened(!1)},this.hideDelay))}__scheduleCooldown(){X=setTimeout(()=>{X=null,J=!1},this.hideDelay)}__scheduleWarmUp(e){const t=e?this.focusDelay:this.hoverDelay;Z=setTimeout(()=>{Z=null,J=!0,this.__showTooltip()},t)}}const hn=o=>class extends ln(o){static get properties(){return{ariaTarget:{type:Object},context:{type:Object,value:()=>({})},focusDelay:{type:Number},for:{type:String,observer:"__forChanged"},generator:{type:Object},hideDelay:{type:Number},hoverDelay:{type:Number},manual:{type:Boolean,value:!1,sync:!0},opened:{type:Boolean,value:!1,sync:!0},position:{type:String},shouldShow:{type:Object,value:()=>(t,i)=>!0},target:{type:Object,observer:"__targetChanged"},text:{type:String,observer:"__textChanged"},_autoOpened:{type:Boolean,observer:"__autoOpenedChanged",sync:!0},_effectiveAriaTarget:{type:Object,computed:"__computeAriaTarget(ariaTarget, target)",observer:"__effectiveAriaTargetChanged"},__effectivePosition:{type:String,computed:"__computePosition(position, _position)"},__isTargetHidden:{type:Boolean,value:!1},_isConnected:{type:Boolean,sync:!0},_position:{type:String,value:"bottom"},_srLabel:{type:Object},_overlayContent:{type:String}}}static get observers(){return["__generatorChanged(_overlayElement, generator, context)","__updateSrLabelText(_srLabel, _overlayContent)"]}static setDefaultFocusDelay(t){Vo=t!=null&&t>=0?t:j}static setDefaultHideDelay(t){jo=t!=null&&t>=0?t:j}static setDefaultHoverDelay(t){Uo=t!=null&&t>=0?t:j}constructor(){super(),this._uniqueId=`vaadin-tooltip-${Bo()}`,this._renderer=this.__tooltipRenderer.bind(this),this.__onFocusin=this.__onFocusin.bind(this),this.__onFocusout=this.__onFocusout.bind(this),this.__onMouseDown=this.__onMouseDown.bind(this),this.__onMouseEnter=this.__onMouseEnter.bind(this),this.__onMouseLeave=this.__onMouseLeave.bind(this),this.__onKeyDown=this.__onKeyDown.bind(this),this.__onOverlayOpen=this.__onOverlayOpen.bind(this),this.__targetVisibilityObserver=new IntersectionObserver(t=>{t.forEach(i=>this.__onTargetVisibilityChange(i.isIntersecting))},{threshold:0}),this._stateController=new un(this)}connectedCallback(){super.connectedCallback(),this._isConnected=!0,document.body.addEventListener("vaadin-overlay-open",this.__onOverlayOpen)}disconnectedCallback(){super.disconnectedCallback(),this._autoOpened&&this._stateController.close(!0),this._isConnected=!1,document.body.removeEventListener("vaadin-overlay-open",this.__onOverlayOpen)}ready(){super.ready(),this._srLabelController=new dt(this,"sr-label","div",{initializer:t=>{t.id=this._uniqueId,t.setAttribute("role","tooltip"),this._srLabel=t}}),this.addController(this._srLabelController)}__computeHorizontalAlign(t){return["top-end","bottom-end","start-top","start","start-bottom"].includes(t)?"end":"start"}__computeNoHorizontalOverlap(t){return["start-top","start","start-bottom","end-top","end","end-bottom"].includes(t)}__computeNoVerticalOverlap(t){return["top-start","top-end","top","bottom-start","bottom","bottom-end"].includes(t)}__computeVerticalAlign(t){return["top-start","top-end","top","start-bottom","end-bottom"].includes(t)?"bottom":"top"}__computeOpened(t,i,r,s){return s&&(t?i:r)}__computePosition(t,i){return t||i}__autoOpenedChanged(t,i){t?document.addEventListener("keydown",this.__onKeyDown,!0):i&&document.removeEventListener("keydown",this.__onKeyDown,!0)}__forChanged(t){t&&(this.__setTargetByIdDebouncer=Y.debounce(this.__setTargetByIdDebouncer,Ho,()=>this.__setTargetById(t)))}__setTargetById(t){if(!this.isConnected)return;const i=this.getRootNode().getElementById(t);i?this.target=i:console.warn(`No element with id="${t}" found to show tooltip.`)}__targetChanged(t,i){i&&(i.removeEventListener("mouseenter",this.__onMouseEnter),i.removeEventListener("mouseleave",this.__onMouseLeave),i.removeEventListener("focusin",this.__onFocusin),i.removeEventListener("focusout",this.__onFocusout),i.removeEventListener("mousedown",this.__onMouseDown),this.__targetVisibilityObserver.unobserve(i)),t&&(t.addEventListener("mouseenter",this.__onMouseEnter),t.addEventListener("mouseleave",this.__onMouseLeave),t.addEventListener("focusin",this.__onFocusin),t.addEventListener("focusout",this.__onFocusout),t.addEventListener("mousedown",this.__onMouseDown),requestAnimationFrame(()=>{this.__targetVisibilityObserver.observe(t)}))}__onFocusin(t){this.manual||No()&&(this.target.contains(t.relatedTarget)||this.__isShouldShow()&&(this.__focusInside=!0,!this.__isTargetHidden&&(!this.__hoverInside||!this._autoOpened)&&this._stateController.open({focus:!0})))}__onFocusout(t){this.manual||this.target.contains(t.relatedTarget)||(this.__focusInside=!1,this.__hoverInside||this._stateController.close(!0))}__onKeyDown(t){t.key==="Escape"&&(t.stopPropagation(),this._stateController.close(!0))}__onMouseDown(){this._stateController.close(!0)}__onMouseEnter(){this.manual||this.__isShouldShow()&&(this.__hoverInside||(this.__hoverInside=!0,!this.__isTargetHidden&&(!this.__focusInside||!this._autoOpened)&&this._stateController.open({hover:!0})))}__onMouseLeave(t){t.relatedTarget!==this._overlayElement&&this.__handleMouseLeave()}__onOverlayMouseEnter(){this._stateController.isClosing&&this._stateController.open({immediate:!0})}__onOverlayMouseLeave(t){t.relatedTarget!==this.target&&this.__handleMouseLeave()}__handleMouseLeave(){this.manual||(this.__hoverInside=!1,this.__focusInside||this._stateController.close())}__onOverlayOpen(){this.manual||this._overlayElement.opened&&!this._overlayElement._last&&this._stateController.close(!0)}__onTargetVisibilityChange(t){const i=this.__isTargetHidden;if(this.__isTargetHidden=!t,i&&t&&(this.__focusInside||this.__hoverInside)){this._stateController.open({immediate:!0});return}!t&&this._autoOpened&&this._stateController.close(!0)}__isShouldShow(){return!(typeof this.shouldShow=="function"&&this.shouldShow(this.target,this.context)!==!0)}__textChanged(t,i){this._overlayElement&&(t||i)&&this._overlayElement.requestContentUpdate()}__tooltipRenderer(t){t.textContent=typeof this.generator=="function"?this.generator(this.context):this.text,this._overlayContent=t.textContent}__computeAriaTarget(t,i){const r=n=>n&&n.nodeType===Node.ELEMENT_NODE;return(Array.isArray(t)?t.some(r):t)?t:i}__effectiveAriaTargetChanged(t,i){i&&[i].flat().forEach(r=>{Do(r,"aria-describedby",this._uniqueId)}),t&&[t].flat().forEach(r=>{fe(r,"aria-describedby",this._uniqueId)})}__generatorChanged(t,i,r){t&&((i!==this.__oldTextGenerator||r!==this.__oldContext)&&t.requestContentUpdate(),this.__oldTextGenerator=i,this.__oldContext=r)}__updateSrLabelText(t,i){t&&(t.textContent=i)}};/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class pn extends hn(io(lt(at(F)))){static get is(){return"vaadin-tooltip"}static get template(){return z`
      <style>
        :host {
          display: none;
        }
      </style>
      <vaadin-tooltip-overlay
        renderer="[[_renderer]]"
        theme$="[[_theme]]"
        opened="[[__computeOpened(manual, opened, _autoOpened, _isConnected)]]"
        position-target="[[target]]"
        position="[[__effectivePosition]]"
        no-horizontal-overlap$="[[__computeNoHorizontalOverlap(__effectivePosition)]]"
        no-vertical-overlap$="[[__computeNoVerticalOverlap(__effectivePosition)]]"
        horizontal-align="[[__computeHorizontalAlign(__effectivePosition)]]"
        vertical-align="[[__computeVerticalAlign(__effectivePosition)]]"
        on-mouseenter="__onOverlayMouseEnter"
        on-mouseleave="__onOverlayMouseLeave"
        modeless
      ></vaadin-tooltip-overlay>

      <slot name="sr-label"></slot>
    `}}P(pn);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */let Ye=0;function qo(o,e=[],t={}){const i=t.moduleId||`custom-style-module-${Ye}`;Ye+=1;const r=document.createElement("dom-module");o&&r.setAttribute("theme-for",o);const s=!!(e.length&&t.moduleId),n=[].concat(t.include||[]);n.length===0?r.__allStyles=e:s||(r.__partialStyles=e),r.innerHTML=`
    <template>
      ${n.map(a=>`<style include=${a}></style>`)}
      ${s?`<style>${e.map(a=>a.cssText).join(`
`)}</style>`:""}
    </template>
  `,r.register(i)}function fn(o){return de(o.querySelector("template")).map(e=>oo(e.textContent))}function mn(){const e=W.prototype.modules;return Object.keys(e).map(t=>{const i=e[t],r=i.getAttribute("theme-for");return i.__allStyles||(i.__allStyles=fn(i).concat(i.__partialStyles||[])),{themeFor:r,moduleId:t,styles:i.__allStyles}})}window.Vaadin||(window.Vaadin={});window.Vaadin.styleModules={getAllThemes:mn,registerStyles:qo};B&&B.length>0&&(B.forEach(o=>{qo(o.themeFor,o.styles,{moduleId:o.moduleId,include:o.include})}),B.length=0);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const _n=_`
  :host {
    --lumo-size-xs: 1.625rem;
    --lumo-size-s: 1.875rem;
    --lumo-size-m: 2.25rem;
    --lumo-size-l: 2.75rem;
    --lumo-size-xl: 3.5rem;

    /* Icons */
    --lumo-icon-size-s: 1.25em;
    --lumo-icon-size-m: 1.5em;
    --lumo-icon-size-l: 2.25em;
    /* For backwards compatibility */
    --lumo-icon-size: var(--lumo-icon-size-m);
  }
`;O("sizing-props",_n);g("vaadin-input-container",_`
    :host {
      background: var(--_background);
      padding: 0 calc(0.375em + var(--_input-container-radius) / 4 - 1px);
      font-weight: 500;
      line-height: 1;
      position: relative;
      cursor: text;
      box-sizing: border-box;
      border-radius:
        /* See https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius#syntax */
        var(--vaadin-input-field-top-start-radius, var(--_input-container-radius))
        var(--vaadin-input-field-top-end-radius, var(--_input-container-radius))
        var(--vaadin-input-field-bottom-end-radius, var(--_input-container-radius))
        var(--vaadin-input-field-bottom-start-radius, var(--_input-container-radius));
      /* Fallback */
      --_input-container-radius: var(--vaadin-input-field-border-radius, var(--lumo-border-radius-m));
      /* Default values */
      --_background: var(--vaadin-input-field-background, var(--lumo-contrast-10pct));
      --_hover-highlight: var(--vaadin-input-field-hover-highlight, var(--lumo-contrast-50pct));
      --_input-border-color: var(--vaadin-input-field-border-color, var(--lumo-contrast-50pct));
      --_icon-color: var(--vaadin-input-field-icon-color, var(--lumo-contrast-60pct));
      --_icon-size: var(--vaadin-input-field-icon-size, var(--lumo-icon-size-m));
      --_invalid-background: var(--vaadin-input-field-invalid-background, var(--lumo-error-color-10pct));
      --_invalid-hover-highlight: var(--vaadin-input-field-invalid-hover-highlight, var(--lumo-error-color-50pct));
    }

    :host([dir='rtl']) {
      border-radius:
        /* Don't use logical props, see https://github.com/vaadin/vaadin-time-picker/issues/145 */
        var(--vaadin-input-field-top-end-radius, var(--_input-container-radius))
        var(--vaadin-input-field-top-start-radius, var(--_input-container-radius))
        var(--vaadin-input-field-bottom-start-radius, var(--_input-container-radius))
        var(--vaadin-input-field-bottom-end-radius, var(--_input-container-radius));
    }

    /* Used for hover and activation effects */
    :host::after {
      content: '';
      position: absolute;
      inset: 0;
      border-radius: inherit;
      pointer-events: none;
      background: var(--_hover-highlight);
      opacity: 0;
      transition: transform 0.15s, opacity 0.2s;
      transform-origin: 100% 0;
    }

    ::slotted(:not([slot$='fix'])) {
      cursor: inherit;
      min-height: var(--lumo-text-field-size, var(--lumo-size-m));
      padding: 0 0.25em;
      --_lumo-text-field-overflow-mask-image: linear-gradient(to left, transparent, #000 1.25em);
      -webkit-mask-image: var(--_lumo-text-field-overflow-mask-image);
      mask-image: var(--_lumo-text-field-overflow-mask-image);
    }

    /* Read-only */
    :host([readonly]) {
      color: var(--lumo-secondary-text-color);
      background-color: transparent;
      cursor: default;
    }

    :host([readonly])::after {
      background-color: transparent;
      opacity: 1;
      border: var(--vaadin-input-field-readonly-border, 1px dashed var(--lumo-contrast-30pct));
    }

    /* Disabled */
    :host([disabled]) {
      background-color: var(--lumo-contrast-5pct);
    }

    :host([disabled]) ::slotted(*) {
      color: var(--lumo-disabled-text-color);
      -webkit-text-fill-color: var(--lumo-disabled-text-color);
    }

    /* Invalid */
    :host([invalid]) {
      background: var(--_invalid-background);
    }

    :host([invalid])::after {
      background: var(--_invalid-hover-highlight);
    }

    /* Slotted icons */
    ::slotted(vaadin-icon) {
      color: var(--_icon-color);
      width: var(--_icon-size);
      height: var(--_icon-size);
    }

    /* Vaadin icons are based on a 16x16 grid (unlike Lumo and Material icons with 24x24), so they look too big by default */
    ::slotted(vaadin-icon[icon^='vaadin:']) {
      padding: 0.25em;
      box-sizing: border-box !important;
    }

    /* Text align */
    :host([dir='rtl']) ::slotted(:not([slot$='fix'])) {
      --_lumo-text-field-overflow-mask-image: linear-gradient(to right, transparent, #000 1.25em);
    }

    @-moz-document url-prefix() {
      :host([dir='rtl']) ::slotted(:not([slot$='fix'])) {
        mask-image: var(--_lumo-text-field-overflow-mask-image);
      }
    }

    :host([theme~='align-left']) ::slotted(:not([slot$='fix'])) {
      text-align: start;
      --_lumo-text-field-overflow-mask-image: none;
    }

    :host([theme~='align-center']) ::slotted(:not([slot$='fix'])) {
      text-align: center;
      --_lumo-text-field-overflow-mask-image: none;
    }

    :host([theme~='align-right']) ::slotted(:not([slot$='fix'])) {
      text-align: end;
      --_lumo-text-field-overflow-mask-image: none;
    }

    @-moz-document url-prefix() {
      /* Firefox is smart enough to align overflowing text to right */
      :host([theme~='align-right']) ::slotted(:not([slot$='fix'])) {
        --_lumo-text-field-overflow-mask-image: linear-gradient(to right, transparent 0.25em, #000 1.5em);
      }
    }

    @-moz-document url-prefix() {
      /* Firefox is smart enough to align overflowing text to right */
      :host([theme~='align-left']) ::slotted(:not([slot$='fix'])) {
        --_lumo-text-field-overflow-mask-image: linear-gradient(to left, transparent 0.25em, #000 1.5em);
      }
    }

    /* RTL specific styles */
    :host([dir='rtl'])::after {
      transform-origin: 0% 0;
    }

    :host([theme~='align-left'][dir='rtl']) ::slotted(:not([slot$='fix'])) {
      --_lumo-text-field-overflow-mask-image: none;
    }

    :host([theme~='align-center'][dir='rtl']) ::slotted(:not([slot$='fix'])) {
      --_lumo-text-field-overflow-mask-image: none;
    }

    :host([theme~='align-right'][dir='rtl']) ::slotted(:not([slot$='fix'])) {
      --_lumo-text-field-overflow-mask-image: none;
    }

    @-moz-document url-prefix() {
      /* Firefox is smart enough to align overflowing text to right */
      :host([theme~='align-right'][dir='rtl']) ::slotted(:not([slot$='fix'])) {
        --_lumo-text-field-overflow-mask-image: linear-gradient(to right, transparent 0.25em, #000 1.5em);
      }
    }

    @-moz-document url-prefix() {
      /* Firefox is smart enough to align overflowing text to right */
      :host([theme~='align-left'][dir='rtl']) ::slotted(:not([slot$='fix'])) {
        --_lumo-text-field-overflow-mask-image: linear-gradient(to left, transparent 0.25em, #000 1.5em);
      }
    }
  `,{moduleId:"lumo-input-container"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const gn=_`
  @font-face {
    font-family: 'lumo-icons';
    src: url(data:application/font-woff;charset=utf-8;base64,d09GRgABAAAAABEgAAsAAAAAIjQAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAABHU1VCAAABCAAAADsAAABUIIslek9TLzIAAAFEAAAAQwAAAFZAIUuKY21hcAAAAYgAAAD4AAADrsCU8d5nbHlmAAACgAAAC2cAABeAWri7U2hlYWQAAA3oAAAAMAAAADZa/6SsaGhlYQAADhgAAAAdAAAAJAbpA35obXR4AAAOOAAAABAAAACspBAAAGxvY2EAAA5IAAAAWAAAAFh57oA4bWF4cAAADqAAAAAfAAAAIAFKAXBuYW1lAAAOwAAAATEAAAIuUUJZCHBvc3QAAA/0AAABKwAAAelm8SzVeJxjYGRgYOBiMGCwY2BycfMJYeDLSSzJY5BiYGGAAJA8MpsxJzM9kYEDxgPKsYBpDiBmg4gCACY7BUgAeJxjYGS+yDiBgZWBgamKaQ8DA0MPhGZ8wGDIyAQUZWBlZsAKAtJcUxgcXjG+0mIO+p/FEMUcxDANKMwIkgMABn8MLQB4nO3SWW6DMABF0UtwCEnIPM/zhLK8LqhfXRybSP14XUYtHV9hGYQwQBNIo3cUIPkhQeM7rib1ekqnXg981XuC1qvy84lzojleh3puxL0hPjGjRU473teloEefAUNGjJkwZcacBUtWrNmwZceeA0dOnLlw5cadB09elPGhGf+j0NTI/65KfXerT6JhqKnpRKtgOpuqaTrtKjPUlqHmhto21I7pL6i6hlqY3q7qGWrfUAeGOjTUkaGODXViqFNDnRnq3FAXhro01JWhrg11Y6hbQ90Z6t5QD4Z6NNSToZ4N9WKoV0O9GerdUB+G+jTUl6GWRvkL24BkEXictVh9bFvVFb/nxvbz+7Rf/N6zHcd2bCfP+Wic1Z9N0jpNHCD9SNqqoVBgbQoMjY+pjA4hNnWa2pV1rHSIif0DGkyT2k10Kmu1Cag6huj4ZpqYBHSqJsTEJgZCG3TaVBFv595nO3ZIv4RIrPPuvefe884599zzO/cRF8G/tgn6CFFImNgkR0ggX8wlspbhSSWSdrC5ozd30s2dw5afzvgtyz9/zG9t1hV4RtF1pXolowvtzc2z6L2aYUQM45jKH9WDTvd1LRDoDASYWhfTzTyvboXz6uZX4ARX5wrF39y+HM2+CJ8d0pkyqBIqoze3D12ez4DrFoYzxI8dWwMrDlZ2DMqQAR9AROsJU+2smlTPaTTco52BVxXa2a2+I8vvqd2dVHm1LoPeTn/AZPRYGthDYOeZjBjKoFsVGulR3lGU95SeCK44oHU7MhWUGUKZDT3oSUcG2GWuh+EDDfUYA/jhIhl0TOsJNYSEu7mQmi3UzfXwZKA4BsVsHLXQYGgJW95qEtpJ1VcW9HiTriZBlFEqxsDjA09yCNUoQxxwd7KWSTt2y3GTKifkqHRCoWZc3m11Wa/dKdFgXD4kSYfkeJBKd8KMz7J8dZn/cGRCcLGDnA2Ge3bKzcvlnTDNthFWLH7Xt80ua5FMjA4WKelWv5Xo16vHuYzpRbJhhdVlftuRK0VlR27D9lu5TF0DPBi60OrHNO0AfP/uRWvhn/U3LXICE+nh+3IHPUJ8JE6GyBjZQLbjGchlrSgYngF8zyrIF4NJD3atUcgWsWunGN/UHX5B5/yg7uF87Nqp4Gf52F3gH73DjEZNRoqCKAr9giQJp5rGJABpiVE2htNhW9R8nw0jqYjCYcY4LIjwYNScf4WN06IZnZCEqsI4cFaQbo4Z1TsZBx40YhXkHOecaYE5oY37IIQ+iJJ+UsDYSun5MuRSBRZRUUhlY2DqOGajOR6zrSU/5My6l2DnusH1GQgnw5BZP7iuYM/ahcfQ7Z8y51ddfutvuwNqWQ0cBYr8fj0U0vsHpwerVaB2sWhXT2NExi2r1KUE2tUuVMnkepVQrxTmpQrZTG4iu8he8iPyM3KcPE/+RP5KPoE2CEAKclCBzXATxkYOtUY/o961PWRqsj0chRrHFBbtrjP9/P0ven5pcbRdpL94vfsy33e5+izuwz3nFLFPVNayPZx/jdG1fOChflFRvYzsW6L18efgLrSWIgvcqnGJYi4skO4xREURjbDuxKke5v0T3Mrzkt2fi31uyZlLLrqIpEuXXsMlgw442Jb0GAxjS1DM20kBoCzHLXm/jEm0IltdcvU0fEW24jgiwwRjVd9u4NJHcIyoHJcwvyVqgqj5hqBJ1ZWSJryh9p56UWhX1XbhRbW2ZopuZWsQd5y8mEQ8M+C6xjRYxZbDKWf5AgY+Qq/l6wSPk16zDFjowYuu+wjx13mfkxbyDDxadYT/LijZyI0THB+6yfLaWsRcO82zo9mWTNtpO18qlorZoIVMwSN40tky5DOQ1MCIAe24mvlsuwIIxPb10+uXDQ4uWz/9m3rj+ql7p6bufZARuPVq5tXtsn6KwfP8Jy0TeWOyNhUJN6mhX5rkUTtUppQWEMNTqEdaCGKFYKJaQrCE4JtDLYOlNEKmO5kBTPGY2A0N2sY3+dVlo1N9ycBsIGtOjQ2p/tlZvzo0ur4v6cOh8NTospB7U/X40KahoU3bGIH97dnwmtHlYffVG3R1YOwKM2vNhrPhCT5zk64sG53oS4b31aYjqe/B7+kQiXBN+b6h21hNUPMq29B8CU4elINdygMPKF1B+WBTG7Z9ZshpN/xwEuuDQZR+nuoo4CDaAiiwXmLpmukMQyPf/JMclqgL1ixZQ/nnP2VbdUODFGt2fgBvL123rlLYu/6A9ckb7F3K0/CyBMEu6aQoPscroCcacVehvyQyCZAsizsWWBkoLC+WAiWnOksLKaeuQDzGuqSk42aiYTiJ4zf9afl17SrqaTO1f+XlZAfIuYcq7/IqYMaMrksOJ6vHkOCPDq943xcCnHqVD9pHFRpMqSPXrIua1WNs+tOz1U+ciTCDpPk+c4QYJIHnYhxP/kVPAq+ahFpVhPcHp8qyarhiF+HsBU9Hrl+UZa876fbKipL0KqB6OdUveErgtOI97fZ63ae9SvWU6k2w1JfwqnUbHsYcFCJFrC/W12zIMMirWYEHxMPs6LGYSdkSZ5TsNP9PCpwnWC3HKZ1lydNjWHC2Mn3l6vL0dHn1ldP3LTSrX+vKrBqv7KmMr8p0SR6P1NqF63or6XRlIyO90f7+kf7+myOhvt4tq7f09oUiTc2/dycGgqFQcCDRLYmi1NL7fk0CknVMxEg/cdfs/TnpJMNkgqwj17B8beVazSrVbU4lG67IZYOCnWrYy3yBR9cyWcChywos3LJBEdhhFoAdYjiw0rLGm0xU5OzoGm5/ZfmHjVZpNNg6SznzGKDdwv2cCtVn6Eaxo12cfxLprpVtTcZ6hVx6dow7Yq7e8LXO8PY9Jgjoze9yCtU5FNbegcKkQMdCbt9au/te4Ebe0jkc0ukUL32eYnTpNs20h0KpUOhZPYwVcfhZnfdqeCvDfXiuCbAoYWcXERPc/mDQD3/hdF+wK4i/xv3kYfprIpAuMkk2kW3kdtS0kBIKpZwp8KxmsCyfM1MFzAss9LBkDxRyThiaqTLwKYKJVTwmWTudMyz+yks09346MDh4m72yOxCKrt1XMlQ1qPVlTEVVQ1ofdK/sCWjtZu9qGwZ8YZ9PPWlo1IV3eW3+U0aXblP39zrt+JPf6UhEQ1rUjNBULN+utyuaDNW34kpAVuSOeMTyWbSNWnooFu+QFNWQ4d/Ox4IPWx41fP/fB/Rjeoz08ezPA9TysMtmnOXfGN7Ui3xIYLDALrlDLOP09qtJuY2OeL0+QZXdRnR1nxRVBF/SOyKKPpcrn9mWzH4rH9IidE+PTNU2182+hOgSItrE1slByS24vaLvJpxOqe4Pduf3HJkZ+jLqUz9rRzB7p8gKcgWZwV1L8JtUS5Z2JxZSOCuBoMTQihMzLbCPA0KqGMAljRQjONklW/wjnXKy8vxT/Elvm3/KiMUMOoV0/vnDYlhec0SMKtt3/kKMyOt33tj2bqxQLsTjSGLl+EAsNhCnTyRGktW55EgCn/A4PlnWn+Mg8bgZrWqHxTbPwMuyy1u5YeZF2SUM7JRhddwRgiRuxpmgJmxn9ZW7XpcF3ViX/ar6ptRpGJ0S9Adg4qhb9sI3vbL7qNJV/y4i07t5TZBiho1imFoMz3gED+CtjYUxvP4SOxov4bFoNPg5aR1e+G4UgDPoedJTpogyCJ7oYvRqoVS0MQAy+CoNEdTDUjok5ZHZL/WtjV7rFj3PKQE3iKp7ou+rIxN3b9LB1dGjeT4cvKo3FrnWpYpuaFd/h3dtV8UeKN1Y9hpR3dt4p0H/zKuPQq0kZQUIIpuDfoiETsnIk+gCWMJZUXHtE8V9LkUc2TE8vOMbO4ax/MACabzyaGXc7u3FBr11ThBdB8SIeMAlCntG2KThHSPsaj2Dc9KNyY2a0KZ7ODaTHoRiFkeYz+shZBpCS4X6471KKKnuHd84edfk5F37d1XO5bbkcltu2ZLNbvnPXiUVAnVvprJrP+NObryjxrllS65md6Tm6wzFHRR4dY3QUUjb7MgxaIixU8hspi98fl/Xc+IB4iU66eCVL9YfAfahiSUt4TONS8x0D8W7u8vd3fGWx6OXlM/U1IoU/s61PGhpyXRFa3eReq2qG56lvmYtXavCC1iN7lbiBpWxXHU+cSlztVLVz0tVN600fVsLxaVDknhYioeoXP3t4lqV1r79MAw0GCI1FTL1YIGzPL1MMlJ9ZsN9P7lvA2yr9ZFUzwzPrVgxN/x/SS+chwB4nGNgZGBgAOLPrYdY4vltvjJwM78AijDUqG5oRND/XzNPZboF5HIwMIFEAU/lC+J4nGNgZGBgDvqfBSRfMAAB81QGRgZUoA0AVvYDbwAAAHicY2BgYGB+MTQwAM8EJo8AAAAAAE4AmgDoAQoBLAFOAXABmgHEAe4CGgKcAugEmgS8BNYE8gUOBSoFegXQBf4GRAZmBrYHGAeQCBgIUghqCP4JRgm+CdoKBAo+CoQKugr0C1QLmgvAeJxjYGRgYNBmTGEQZQABJiDmAkIGhv9gPgMAGJQBvAB4nG2RPU7DMBiG3/QP0UoIBGJh8QILavozdmRo9w7d09RpUzlx5LgVvQMn4BAcgoEzcAgOwVvzSZVQbcnf48fvFysJgGt8IcJxROiG9TgauODuj5ukG+EW+UG4jR4ehTv0Q+EunjER7uEWmk+IWpc0d3gVbuAKb8JN+nfhFvlDuI17fAp36L+Fu1jgR7iHp+jF7Arbz1Nb1nO93pnEncSJFtrVuS3VKB6e5EyX2iVer9TyoOr9eux9pjJnCzW1pdfGWFU5u9WpjzfeV5PBIBMfp7aAwQ4FLPrIkbKWqDHn+67pDRK4s4lzbsEux5qHvcIIMb/nueSMyTKkE3jWFdNLHLjW2PPmMa1Hxn3GjGW/wjT0HtOG09JU4WxLk9LH2ISuiv9twJn9y8fh9uIXI+BknAAAAHicbY7ZboMwEEW5CVBCSLrv+76kfJRjTwHFsdGAG+Xvy5JUfehIHp0rnxmNN/D6ir3/a4YBhvARIMQOIowQY4wEE0yxiz3s4wCHOMIxTnCKM5zjApe4wjVucIs73OMBj3jCM17wije84wMzfHqJ0EVmUkmmJo77oOmrHvfIRZbXsTCZplTZldlgb3TYGVHProwFs11t1A57tcON2rErR3PBqcwF1/6ctI6k0GSU4JHMSS6WghdJQ99sTbfuN7QLJ9vQ37dNrgyktnIxlDYLJNuqitpRbYWKFNuyDT6pog6oOYKHtKakeakqKjHXpPwlGRcsC+OqxLIiJpXqoqqDMreG2l5bv9Ri3TRX+c23DZna9WFFgmXuO6Ps1Jm/w6ErW8N3FbHn/QC444j0AA==)
      format('woff');
    font-weight: normal;
    font-style: normal;
  }

  html {
    --lumo-icons-align-center: '\\ea01';
    --lumo-icons-align-left: '\\ea02';
    --lumo-icons-align-right: '\\ea03';
    --lumo-icons-angle-down: '\\ea04';
    --lumo-icons-angle-left: '\\ea05';
    --lumo-icons-angle-right: '\\ea06';
    --lumo-icons-angle-up: '\\ea07';
    --lumo-icons-arrow-down: '\\ea08';
    --lumo-icons-arrow-left: '\\ea09';
    --lumo-icons-arrow-right: '\\ea0a';
    --lumo-icons-arrow-up: '\\ea0b';
    --lumo-icons-bar-chart: '\\ea0c';
    --lumo-icons-bell: '\\ea0d';
    --lumo-icons-calendar: '\\ea0e';
    --lumo-icons-checkmark: '\\ea0f';
    --lumo-icons-chevron-down: '\\ea10';
    --lumo-icons-chevron-left: '\\ea11';
    --lumo-icons-chevron-right: '\\ea12';
    --lumo-icons-chevron-up: '\\ea13';
    --lumo-icons-clock: '\\ea14';
    --lumo-icons-cog: '\\ea15';
    --lumo-icons-cross: '\\ea16';
    --lumo-icons-download: '\\ea17';
    --lumo-icons-dropdown: '\\ea18';
    --lumo-icons-edit: '\\ea19';
    --lumo-icons-error: '\\ea1a';
    --lumo-icons-eye: '\\ea1b';
    --lumo-icons-eye-disabled: '\\ea1c';
    --lumo-icons-menu: '\\ea1d';
    --lumo-icons-minus: '\\ea1e';
    --lumo-icons-ordered-list: '\\ea1f';
    --lumo-icons-phone: '\\ea20';
    --lumo-icons-photo: '\\ea21';
    --lumo-icons-play: '\\ea22';
    --lumo-icons-plus: '\\ea23';
    --lumo-icons-redo: '\\ea24';
    --lumo-icons-reload: '\\ea25';
    --lumo-icons-search: '\\ea26';
    --lumo-icons-undo: '\\ea27';
    --lumo-icons-unordered-list: '\\ea28';
    --lumo-icons-upload: '\\ea29';
    --lumo-icons-user: '\\ea2a';
  }
`;O("font-icons",gn);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Wo=_`
  [part$='button'] {
    flex: none;
    width: 1em;
    height: 1em;
    line-height: 1;
    font-size: var(--lumo-icon-size-m);
    text-align: center;
    color: var(--lumo-contrast-60pct);
    transition: 0.2s color;
    cursor: var(--lumo-clickable-cursor);
  }

  [part$='button']:hover {
    color: var(--lumo-contrast-90pct);
  }

  :host([disabled]) [part$='button'],
  :host([readonly]) [part$='button'] {
    color: var(--lumo-contrast-20pct);
    cursor: default;
  }

  [part$='button']::before {
    font-family: 'lumo-icons';
    display: block;
  }
`;g("",Wo,{moduleId:"lumo-field-button"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const vn=_`
  :host {
    --_helper-spacing: var(--vaadin-input-field-helper-spacing, 0.4em);
  }

  :host([has-helper]) [part='helper-text']::before {
    content: '';
    display: block;
    height: var(--_helper-spacing);
  }

  [part='helper-text'] {
    display: block;
    color: var(--vaadin-input-field-helper-color, var(--lumo-secondary-text-color));
    font-size: var(--vaadin-input-field-helper-font-size, var(--lumo-font-size-xs));
    line-height: var(--lumo-line-height-xs);
    font-weight: var(--vaadin-input-field-helper-font-weight, 400);
    margin-left: calc(var(--lumo-border-radius-m) / 4);
    transition: color 0.2s;
  }

  :host(:hover:not([readonly])) [part='helper-text'] {
    color: var(--lumo-body-text-color);
  }

  :host([disabled]) [part='helper-text'] {
    color: var(--lumo-disabled-text-color);
    -webkit-text-fill-color: var(--lumo-disabled-text-color);
  }

  :host([has-helper][theme~='helper-above-field']) [part='helper-text']::before {
    display: none;
  }

  :host([has-helper][theme~='helper-above-field']) [part='helper-text']::after {
    content: '';
    display: block;
    height: var(--_helper-spacing);
  }

  :host([has-helper][theme~='helper-above-field']) [part='label'] {
    order: 0;
    padding-bottom: var(--_helper-spacing);
  }

  :host([has-helper][theme~='helper-above-field']) [part='helper-text'] {
    order: 1;
  }

  :host([has-helper][theme~='helper-above-field']) [part='label'] + * {
    order: 2;
  }

  :host([has-helper][theme~='helper-above-field']) [part='error-message'] {
    order: 3;
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Yo=_`
  [part='label'] {
    align-self: flex-start;
    color: var(--vaadin-input-field-label-color, var(--lumo-secondary-text-color));
    font-weight: var(--vaadin-input-field-label-font-weight, 500);
    font-size: var(--vaadin-input-field-label-font-size, var(--lumo-font-size-s));
    margin-left: calc(var(--lumo-border-radius-m) / 4);
    transition: color 0.2s;
    line-height: 1;
    padding-right: 1em;
    padding-bottom: 0.5em;
    /* As a workaround for diacritics being cut off, add a top padding and a
    negative margin to compensate */
    padding-top: 0.25em;
    margin-top: -0.25em;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    position: relative;
    max-width: 100%;
    box-sizing: border-box;
  }

  :host([focused]:not([readonly])) [part='label'] {
    color: var(--vaadin-input-field-focused-label-color, var(--lumo-primary-text-color));
  }

  :host(:hover:not([readonly]):not([focused])) [part='label'] {
    color: var(--vaadin-input-field-hovered-label-color, var(--lumo-body-text-color));
  }

  /* Touch device adjustment */
  @media (pointer: coarse) {
    :host(:hover:not([readonly]):not([focused])) [part='label'] {
      color: var(--vaadin-input-field-label-color, var(--lumo-secondary-text-color));
    }
  }

  :host([has-label])::before {
    margin-top: calc(var(--lumo-font-size-s) * 1.5);
  }

  :host([has-label][theme~='small'])::before {
    margin-top: calc(var(--lumo-font-size-xs) * 1.5);
  }

  :host([has-label]) {
    padding-top: var(--lumo-space-m);
  }

  :host([has-label]) ::slotted([slot='tooltip']) {
    --vaadin-tooltip-offset-bottom: calc((var(--lumo-space-m) - var(--lumo-space-xs)) * -1);
  }

  :host([required]) [part='required-indicator']::after {
    content: var(--lumo-required-field-indicator, '\\2022');
    transition: opacity 0.2s;
    color: var(--lumo-required-field-indicator-color, var(--lumo-primary-text-color));
    position: absolute;
    right: 0;
    width: 1em;
    text-align: center;
  }

  :host([invalid]) [part='required-indicator']::after {
    color: var(--lumo-required-field-indicator-color, var(--lumo-error-text-color));
  }

  [part='error-message'] {
    margin-left: calc(var(--lumo-border-radius-m) / 4);
    font-size: var(--vaadin-input-field-error-font-size, var(--lumo-font-size-xs));
    line-height: var(--lumo-line-height-xs);
    font-weight: var(--vaadin-input-field-error-font-weight, 400);
    color: var(--vaadin-input-field-error-color, var(--lumo-error-text-color));
    will-change: max-height;
    transition: 0.4s max-height;
    max-height: 5em;
  }

  :host([has-error-message]) [part='error-message']::before,
  :host([has-error-message]) [part='error-message']::after {
    content: '';
    display: block;
    height: 0.4em;
  }

  :host(:not([invalid])) [part='error-message'] {
    max-height: 0;
    overflow: hidden;
  }

  /* RTL specific styles */

  :host([dir='rtl']) [part='label'] {
    margin-left: 0;
    margin-right: calc(var(--lumo-border-radius-m) / 4);
  }

  :host([dir='rtl']) [part='label'] {
    padding-left: 1em;
    padding-right: 0;
  }

  :host([dir='rtl']) [part='required-indicator']::after {
    right: auto;
    left: 0;
  }

  :host([dir='rtl']) [part='error-message'] {
    margin-left: 0;
    margin-right: calc(var(--lumo-border-radius-m) / 4);
  }
`;g("",Yo,{moduleId:"lumo-required-field"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const bn=_`
  :host {
    --lumo-text-field-size: var(--lumo-size-m);
    color: var(--vaadin-input-field-value-color, var(--lumo-body-text-color));
    font-size: var(--vaadin-input-field-value-font-size, var(--lumo-font-size-m));
    font-weight: var(--vaadin-input-field-value-font-weight, 400);
    font-family: var(--lumo-font-family);
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    -webkit-tap-highlight-color: transparent;
    padding: var(--lumo-space-xs) 0;
    --_focus-ring-color: var(--vaadin-focus-ring-color, var(--lumo-primary-color-50pct));
    --_focus-ring-width: var(--vaadin-focus-ring-width, 2px);
    --_input-height: var(--vaadin-input-field-height, var(--lumo-text-field-size));
  }

  :host::before {
    height: var(--_input-height);
    box-sizing: border-box;
    display: inline-flex;
    align-items: center;
  }

  :host([focused]) [part='input-field'] ::slotted(:is(input, textarea)) {
    -webkit-mask-image: none;
    mask-image: none;
  }

  ::slotted(:is(input, textarea):placeholder-shown) {
    color: var(--vaadin-input-field-placeholder-color, var(--lumo-secondary-text-color));
  }

  /* Hover */
  :host(:hover:not([readonly]):not([focused])) [part='input-field']::after {
    opacity: var(--vaadin-input-field-hover-highlight-opacity, 0.1);
  }

  /* Touch device adjustment */
  @media (pointer: coarse) {
    :host(:hover:not([readonly]):not([focused])) [part='input-field']::after {
      opacity: 0;
    }

    :host(:active:not([readonly]):not([focused])) [part='input-field']::after {
      opacity: 0.2;
    }
  }

  /* Trigger when not focusing using the keyboard */
  :host([focused]:not([focus-ring]):not([readonly])) [part='input-field']::after {
    transform: scaleX(0);
    transition-duration: 0.15s, 1s;
  }

  /* Focus-ring */
  :host([focus-ring]) [part='input-field'] {
    box-shadow: 0 0 0 var(--_focus-ring-width) var(--_focus-ring-color);
  }

  /* Read-only and disabled */
  :host(:is([readonly], [disabled])) ::slotted(:is(input, textarea):placeholder-shown) {
    opacity: 0;
  }

  /* Read-only style */
  :host([readonly]) {
    --vaadin-input-field-border-color: transparent;
  }

  /* Disabled style */
  :host([disabled]) {
    pointer-events: none;
    --vaadin-input-field-border-color: var(--lumo-contrast-20pct);
  }

  :host([disabled]) [part='label'],
  :host([disabled]) [part='input-field'] ::slotted(*) {
    color: var(--lumo-disabled-text-color);
    -webkit-text-fill-color: var(--lumo-disabled-text-color);
  }

  /* Invalid style */
  :host([invalid]) {
    --vaadin-input-field-border-color: var(--lumo-error-color);
  }

  :host([invalid][focus-ring]) [part='input-field'] {
    box-shadow: 0 0 0 2px var(--lumo-error-color-50pct);
  }

  :host([input-prevented]) [part='input-field'] {
    animation: shake 0.15s infinite;
  }

  @keyframes shake {
    25% {
      transform: translateX(4px);
    }
    75% {
      transform: translateX(-4px);
    }
  }

  /* Small theme */
  :host([theme~='small']) {
    font-size: var(--lumo-font-size-s);
    --lumo-text-field-size: var(--lumo-size-s);
  }

  :host([theme~='small']) [part='label'] {
    font-size: var(--lumo-font-size-xs);
  }

  :host([theme~='small']) [part='error-message'] {
    font-size: var(--lumo-font-size-xxs);
  }

  /* Slotted content */
  [part='input-field'] ::slotted(:not(vaadin-icon):not(input):not(textarea)) {
    color: var(--lumo-secondary-text-color);
    font-weight: 400;
  }

  [part='clear-button']::before {
    content: var(--lumo-icons-cross);
  }
`,St=[Yo,Wo,vn,bn];g("",St,{moduleId:"lumo-input-field-shared-styles"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-text-field",St,{moduleId:"lumo-text-field-styles"});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const yn=o=>class extends o{static get properties(){return{disabled:{type:Boolean,reflectToAttribute:!0},readonly:{type:Boolean,reflectToAttribute:!0},invalid:{type:Boolean,reflectToAttribute:!0}}}ready(){super.ready(),this.addEventListener("pointerdown",t=>{t.target===this&&t.preventDefault()}),this.addEventListener("click",t=>{t.target===this&&this.shadowRoot.querySelector("slot:not([name])").assignedNodes({flatten:!0}).forEach(i=>i.focus&&i.focus())})}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const xn=_`
  :host {
    display: flex;
    align-items: center;
    flex: 0 1 auto;
    border-radius:
            /* See https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius */
      var(--vaadin-input-field-top-start-radius, var(--__border-radius))
      var(--vaadin-input-field-top-end-radius, var(--__border-radius))
      var(--vaadin-input-field-bottom-end-radius, var(--__border-radius))
      var(--vaadin-input-field-bottom-start-radius, var(--__border-radius));
    --_border-radius: var(--vaadin-input-field-border-radius, 0);
    --_input-border-width: var(--vaadin-input-field-border-width, 0);
    --_input-border-color: var(--vaadin-input-field-border-color, transparent);
    box-shadow: inset 0 0 0 var(--_input-border-width, 0) var(--_input-border-color);
  }

  :host([dir='rtl']) {
    border-radius:
            /* Don't use logical props, see https://github.com/vaadin/vaadin-time-picker/issues/145 */
      var(--vaadin-input-field-top-end-radius, var(--_border-radius))
      var(--vaadin-input-field-top-start-radius, var(--_border-radius))
      var(--vaadin-input-field-bottom-start-radius, var(--_border-radius))
      var(--vaadin-input-field-bottom-end-radius, var(--_border-radius));
  }

  :host([hidden]) {
    display: none !important;
  }

  /* Reset the native input styles */
  ::slotted(input) {
    -webkit-appearance: none;
    -moz-appearance: none;
    flex: auto;
    white-space: nowrap;
    overflow: hidden;
    width: 100%;
    height: 100%;
    outline: none;
    margin: 0;
    padding: 0;
    border: 0;
    border-radius: 0;
    min-width: 0;
    font: inherit;
    line-height: normal;
    color: inherit;
    background-color: transparent;
    /* Disable default invalid style in Firefox */
    box-shadow: none;
  }

  ::slotted(*) {
    flex: none;
  }

  ::slotted(:is(input, textarea))::placeholder {
    /* Use ::slotted(input:placeholder-shown) in themes to style the placeholder. */
    /* because ::slotted(...)::placeholder does not work in Safari. */
    font: inherit;
    color: inherit;
    /* Override default opacity in Firefox */
    opacity: 1;
  }
`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-input-container",xn,{moduleId:"vaadin-input-container-styles"});class wn extends yn(nt(ue(F))){static get is(){return"vaadin-input-container"}static get template(){return z`
      <slot name="prefix"></slot>
      <slot></slot>
      <slot name="suffix"></slot>
    `}}P(wn);/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Go extends dt{constructor(e){super(e,"tooltip"),this.setTarget(e)}initCustomNode(e){e.target=this.target,this.ariaTarget!==void 0&&(e.ariaTarget=this.ariaTarget),this.context!==void 0&&(e.context=this.context),this.manual!==void 0&&(e.manual=this.manual),this.opened!==void 0&&(e.opened=this.opened),this.position!==void 0&&(e._position=this.position),this.shouldShow!==void 0&&(e.shouldShow=this.shouldShow),this.__notifyChange()}teardownNode(){this.__notifyChange()}setAriaTarget(e){this.ariaTarget=e;const t=this.node;t&&(t.ariaTarget=e)}setContext(e){this.context=e;const t=this.node;t&&(t.context=e)}setManual(e){this.manual=e;const t=this.node;t&&(t.manual=e)}setOpened(e){this.opened=e;const t=this.node;t&&(t.opened=e)}setPosition(e){this.position=e;const t=this.node;t&&(t._position=e)}setShouldShow(e){this.shouldShow=e;const t=this.node;t&&(t.shouldShow=e)}setTarget(e){this.target=e;const t=this.node;t&&(t.target=e)}__notifyChange(){this.dispatchEvent(new CustomEvent("tooltip-changed",{detail:{node:this.node}}))}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd..
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Cn=_`
  [part='clear-button'] {
    display: none;
    cursor: default;
  }

  [part='clear-button']::before {
    content: '\\2715';
  }

  :host([clear-button-visible][has-value]:not([disabled]):not([readonly])) [part='clear-button'] {
    display: block;
  }
`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd..
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const An=_`
  :host {
    display: inline-flex;
    outline: none;
  }

  :host::before {
    content: '\\2003';
    width: 0;
    display: inline-block;
    /* Size and position this element on the same vertical position as the input-field element
          to make vertical align for the host element work as expected */
  }

  :host([hidden]) {
    display: none !important;
  }

  :host(:not([has-label])) [part='label'] {
    display: none;
  }

  @media (forced-colors: active) {
    :host(:not([readonly])) [part='input-field'] {
      outline: 1px solid;
      outline-offset: -1px;
    }
    :host([focused]) [part='input-field'] {
      outline-width: 2px;
    }
    :host([disabled]) [part='input-field'] {
      outline-color: GrayText;
    }
  }
`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd..
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const En=_`
  [class$='container'] {
    display: flex;
    flex-direction: column;
    min-width: 100%;
    max-width: 100%;
    width: var(--vaadin-field-default-width, 12em);
  }
`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd..
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Pn=[An,En,Cn];/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Tn extends dt{constructor(e,t){super(e,"input","input",{initializer:(i,r)=>{r.value&&(i.value=r.value),r.type&&i.setAttribute("type",r.type),i.id=this.defaultId,typeof t=="function"&&t(i)},useUniqueId:!0})}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const $o=v(o=>class extends o{get _keyboardActive(){return No()}ready(){this.addEventListener("focusin",t=>{this._shouldSetFocus(t)&&this._setFocused(!0)}),this.addEventListener("focusout",t=>{this._shouldRemoveFocus(t)&&this._setFocused(!1)}),super.ready()}disconnectedCallback(){super.disconnectedCallback(),this.hasAttribute("focused")&&this._setFocused(!1)}_setFocused(t){this.toggleAttribute("focused",t),this.toggleAttribute("focus-ring",t&&this._keyboardActive)}_shouldSetFocus(t){return!0}_shouldRemoveFocus(t){return!0}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ko=v(o=>class extends o{static get properties(){return{disabled:{type:Boolean,value:!1,observer:"_disabledChanged",reflectToAttribute:!0}}}_disabledChanged(t){this._setAriaDisabled(t)}_setAriaDisabled(t){t?this.setAttribute("aria-disabled","true"):this.removeAttribute("aria-disabled")}click(){this.disabled||super.click()}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Jo=o=>class extends Ko(o){static get properties(){return{tabindex:{type:Number,reflectToAttribute:!0,observer:"_tabindexChanged"},_lastTabIndex:{type:Number}}}_disabledChanged(t,i){super._disabledChanged(t,i),t?(this.tabindex!==void 0&&(this._lastTabIndex=this.tabindex),this.tabindex=-1):i&&(this.tabindex=this._lastTabIndex)}_tabindexChanged(t){this.disabled&&t!==-1&&(this._lastTabIndex=t,this.tabindex=-1)}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Sn=v(o=>class extends $o(Jo(o)){static get properties(){return{autofocus:{type:Boolean},focusElement:{type:Object,readOnly:!0,observer:"_focusElementChanged"},_lastTabIndex:{value:0}}}constructor(){super(),this._boundOnBlur=this._onBlur.bind(this),this._boundOnFocus=this._onFocus.bind(this)}ready(){super.ready(),this.autofocus&&!this.disabled&&requestAnimationFrame(()=>{this.focus(),this.setAttribute("focus-ring","")})}focus(){this.focusElement&&!this.disabled&&this.focusElement.focus()}blur(){this.focusElement&&this.focusElement.blur()}click(){this.focusElement&&!this.disabled&&this.focusElement.click()}_focusElementChanged(t,i){t?(t.disabled=this.disabled,this._addFocusListeners(t),this.__forwardTabIndex(this.tabindex)):i&&this._removeFocusListeners(i)}_addFocusListeners(t){t.addEventListener("blur",this._boundOnBlur),t.addEventListener("focus",this._boundOnFocus)}_removeFocusListeners(t){t.removeEventListener("blur",this._boundOnBlur),t.removeEventListener("focus",this._boundOnFocus)}_onFocus(t){t.stopPropagation(),this.dispatchEvent(new Event("focus"))}_onBlur(t){t.stopPropagation(),this.dispatchEvent(new Event("blur"))}_shouldSetFocus(t){return t.target===this.focusElement}_shouldRemoveFocus(t){return t.target===this.focusElement}_disabledChanged(t,i){super._disabledChanged(t,i),this.focusElement&&(this.focusElement.disabled=t),t&&this.blur()}_tabindexChanged(t){this.__forwardTabIndex(t)}__forwardTabIndex(t){t!==void 0&&this.focusElement&&(this.focusElement.tabIndex=t,t!==-1&&(this.tabindex=void 0)),this.disabled&&t&&(t!==-1&&(this._lastTabIndex=t),this.tabindex=void 0)}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const me=v(o=>class extends o{ready(){super.ready(),this.addEventListener("keydown",t=>{this._onKeyDown(t)}),this.addEventListener("keyup",t=>{this._onKeyUp(t)})}_onKeyDown(t){switch(t.key){case"Enter":this._onEnter(t);break;case"Escape":this._onEscape(t);break}}_onKeyUp(t){}_onEnter(t){}_onEscape(t){}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ht=new WeakMap;function kn(o){return Ht.has(o)||Ht.set(o,new Set),Ht.get(o)}function On(o,e){const t=document.createElement("style");t.textContent=o,e===document?document.head.appendChild(t):e.insertBefore(t,e.firstChild)}const zn=v(o=>class extends o{get slotStyles(){return{}}connectedCallback(){super.connectedCallback(),this.__applySlotStyles()}__applySlotStyles(){const t=this.getRootNode(),i=kn(t);this.slotStyles.forEach(r=>{i.has(r)||(On(r,t),i.add(r))})}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Zo=v(o=>class extends o{static get properties(){return{inputElement:{type:Object,readOnly:!0,observer:"_inputElementChanged"},type:{type:String,readOnly:!0},value:{type:String,value:"",observer:"_valueChanged",notify:!0,sync:!0},_hasInputValue:{type:Boolean,value:!1,observer:"_hasInputValueChanged"}}}constructor(){super(),this._boundOnInput=this.__onInput.bind(this),this._boundOnChange=this._onChange.bind(this)}get _hasValue(){return this.value!=null&&this.value!==""}get _inputElementValueProperty(){return"value"}get _inputElementValue(){return this.inputElement?this.inputElement[this._inputElementValueProperty]:void 0}set _inputElementValue(t){this.inputElement&&(this.inputElement[this._inputElementValueProperty]=t)}clear(){this._hasInputValue=!1,this.value="",this._inputElementValue=""}_addInputListeners(t){t.addEventListener("input",this._boundOnInput),t.addEventListener("change",this._boundOnChange)}_removeInputListeners(t){t.removeEventListener("input",this._boundOnInput),t.removeEventListener("change",this._boundOnChange)}_forwardInputValue(t){this.inputElement&&(this._inputElementValue=t??"")}_inputElementChanged(t,i){t?this._addInputListeners(t):i&&this._removeInputListeners(i)}_hasInputValueChanged(t,i){(t||i)&&this.dispatchEvent(new CustomEvent("has-input-value-changed"))}__onInput(t){this._setHasInputValue(t),this._onInput(t)}_onInput(t){const i=t.composedPath()[0];this.__userInput=t.isTrusted,this.value=i.value,this.__userInput=!1}_onChange(t){}_toggleHasValue(t){this.toggleAttribute("has-value",t)}_valueChanged(t,i){this._toggleHasValue(this._hasValue),!(t===""&&i===void 0)&&(this.__userInput||this._forwardInputValue(t))}_setHasInputValue(t){const i=t.composedPath()[0];this._hasInputValue=i.value.length>0}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Mn=o=>class extends Zo(me(o)){static get properties(){return{clearButtonVisible:{type:Boolean,reflectToAttribute:!0,value:!1}}}get clearElement(){return console.warn(`Please implement the 'clearElement' property in <${this.localName}>`),null}ready(){super.ready(),this.clearElement&&(this.clearElement.addEventListener("mousedown",t=>this._onClearButtonMouseDown(t)),this.clearElement.addEventListener("click",t=>this._onClearButtonClick(t)))}_onClearButtonClick(t){t.preventDefault(),this._onClearAction()}_onClearButtonMouseDown(t){t.preventDefault(),Cs||this.inputElement.focus()}_onEscape(t){super._onEscape(t),this.clearButtonVisible&&this.value&&(t.stopPropagation(),this._onClearAction())}_onClearAction(){this._inputElementValue="",this.inputElement.dispatchEvent(new Event("input",{bubbles:!0,composed:!0})),this.inputElement.dispatchEvent(new Event("change",{bubbles:!0}))}};/**
 * @license
 * Copyright (c) 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Bt=new Map;function _e(o){return Bt.has(o)||Bt.set(o,new WeakMap),Bt.get(o)}function Xo(o,e){o&&o.removeAttribute(e)}function Qo(o,e){if(!o||!e)return;const t=_e(e);if(t.has(o))return;const i=pe(o.getAttribute(e));t.set(o,new Set(i))}function Nn(o,e){if(!o||!e)return;const t=_e(e),i=t.get(o);!i||i.size===0?o.removeAttribute(e):fe(o,e,Tt(i)),t.delete(o)}function Vt(o,e,t={newId:null,oldId:null,fromUser:!1}){if(!o||!e)return;const{newId:i,oldId:r,fromUser:s}=t,n=_e(e),a=n.get(o);if(!s&&a){r&&a.delete(r),i&&a.add(i);return}s&&(a?i||n.delete(o):Qo(o,e),Xo(o,e)),Do(o,e,r);const l=i||Tt(a);l&&fe(o,e,l)}function In(o,e){Qo(o,e),Xo(o,e)}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Ln{constructor(e){this.host=e,this.__required=!1}setTarget(e){this.__target=e,this.__setAriaRequiredAttribute(this.__required),this.__setLabelIdToAriaAttribute(this.__labelId,this.__labelId),this.__labelIdFromUser!=null&&this.__setLabelIdToAriaAttribute(this.__labelIdFromUser,this.__labelIdFromUser,!0),this.__setErrorIdToAriaAttribute(this.__errorId),this.__setHelperIdToAriaAttribute(this.__helperId),this.setAriaLabel(this.__label)}setRequired(e){this.__setAriaRequiredAttribute(e),this.__required=e}setAriaLabel(e){this.__setAriaLabelToAttribute(e),this.__label=e}setLabelId(e,t=!1){const i=t?this.__labelIdFromUser:this.__labelId;this.__setLabelIdToAriaAttribute(e,i,t),t?this.__labelIdFromUser=e:this.__labelId=e}setErrorId(e){this.__setErrorIdToAriaAttribute(e,this.__errorId),this.__errorId=e}setHelperId(e){this.__setHelperIdToAriaAttribute(e,this.__helperId),this.__helperId=e}__setAriaLabelToAttribute(e){this.__target&&(e?(In(this.__target,"aria-labelledby"),this.__target.setAttribute("aria-label",e)):this.__label&&(Nn(this.__target,"aria-labelledby"),this.__target.removeAttribute("aria-label")))}__setLabelIdToAriaAttribute(e,t,i){Vt(this.__target,"aria-labelledby",{newId:e,oldId:t,fromUser:i})}__setErrorIdToAriaAttribute(e,t){Vt(this.__target,"aria-describedby",{newId:e,oldId:t,fromUser:!1})}__setHelperIdToAriaAttribute(e,t){Vt(this.__target,"aria-describedby",{newId:e,oldId:t,fromUser:!1})}__setAriaRequiredAttribute(e){this.__target&&(["input","textarea"].includes(this.__target.localName)||(e?this.__target.setAttribute("aria-required","true"):this.__target.removeAttribute("aria-required")))}}/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class ge extends dt{constructor(e,t,i,r={}){super(e,t,i,{...r,useUniqueId:!0})}initCustomNode(e){this.__updateNodeId(e),this.__notifyChange(e)}teardownNode(e){const t=this.getSlotChild();t&&t!==this.defaultNode?this.__notifyChange(t):(this.restoreDefaultNode(),this.updateDefaultNode(this.node))}attachDefaultNode(){const e=super.attachDefaultNode();return e&&this.__updateNodeId(e),e}restoreDefaultNode(){}updateDefaultNode(e){this.__notifyChange(e)}observeNode(e){this.__nodeObserver&&this.__nodeObserver.disconnect(),this.__nodeObserver=new MutationObserver(t=>{t.forEach(i=>{const r=i.target,s=r===this.node;i.type==="attributes"?s&&this.__updateNodeId(r):(s||r.parentElement===this.node)&&this.__notifyChange(this.node)})}),this.__nodeObserver.observe(e,{attributes:!0,attributeFilter:["id"],childList:!0,subtree:!0,characterData:!0})}__hasContent(e){return e?e.nodeType===Node.ELEMENT_NODE&&(customElements.get(e.localName)||e.children.length>0)||e.textContent&&e.textContent.trim()!=="":!1}__notifyChange(e){this.dispatchEvent(new CustomEvent("slot-content-changed",{detail:{hasContent:this.__hasContent(e),node:e}}))}__updateNodeId(e){const t=!this.nodes||e===this.nodes[0];e.nodeType===Node.ELEMENT_NODE&&(!this.multiple||t)&&!e.id&&(e.id=this.defaultId)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Dn extends ge{constructor(e){super(e,"error-message","div")}setErrorMessage(e){this.errorMessage=e,this.updateDefaultNode(this.node)}setInvalid(e){this.invalid=e,this.updateDefaultNode(this.node)}initAddedNode(e){e!==this.defaultNode&&this.initCustomNode(e)}initNode(e){this.updateDefaultNode(e)}initCustomNode(e){e.textContent&&!this.errorMessage&&(this.errorMessage=e.textContent.trim()),super.initCustomNode(e)}restoreDefaultNode(){this.attachDefaultNode()}updateDefaultNode(e){const{errorMessage:t,invalid:i}=this,r=!!(i&&t&&t.trim()!=="");e&&(e.textContent=r?t:"",e.hidden=!r,r?e.setAttribute("role","alert"):e.removeAttribute("role")),super.updateDefaultNode(e)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Fn extends ge{constructor(e){super(e,"helper",null)}setHelperText(e){this.helperText=e,this.getSlotChild()||this.restoreDefaultNode(),this.node===this.defaultNode&&this.updateDefaultNode(this.node)}restoreDefaultNode(){const{helperText:e}=this;if(e&&e.trim()!==""){this.tagName="div";const t=this.attachDefaultNode();this.observeNode(t)}}updateDefaultNode(e){e&&(e.textContent=this.helperText),super.updateDefaultNode(e)}initCustomNode(e){super.initCustomNode(e),this.observeNode(e)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Rn extends ge{constructor(e){super(e,"label","label")}setLabel(e){this.label=e,this.getSlotChild()||this.restoreDefaultNode(),this.node===this.defaultNode&&this.updateDefaultNode(this.node)}restoreDefaultNode(){const{label:e}=this;if(e&&e.trim()!==""){const t=this.attachDefaultNode();this.observeNode(t)}}updateDefaultNode(e){e&&(e.textContent=this.label),super.updateDefaultNode(e)}initCustomNode(e){super.initCustomNode(e),this.observeNode(e)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Hn=v(o=>class extends at(o){static get properties(){return{label:{type:String,observer:"_labelChanged"}}}constructor(){super(),this._labelController=new Rn(this),this._labelController.addEventListener("slot-content-changed",t=>{this.toggleAttribute("has-label",t.detail.hasContent)})}get _labelId(){const t=this._labelNode;return t&&t.id}get _labelNode(){return this._labelController.node}ready(){super.ready(),this.addController(this._labelController)}_labelChanged(t){this._labelController.setLabel(t)}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ti=v(o=>class extends o{static get properties(){return{invalid:{type:Boolean,reflectToAttribute:!0,notify:!0,value:!1},required:{type:Boolean,reflectToAttribute:!0}}}validate(){const t=this.checkValidity();return this._setInvalid(!t),this.dispatchEvent(new CustomEvent("validated",{detail:{valid:t}})),t}checkValidity(){return!this.required||!!this.value}_setInvalid(t){this._shouldSetInvalid(t)&&(this.invalid=t)}_shouldSetInvalid(t){return!0}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Bn=o=>class extends ti(Hn(at(o))){static get properties(){return{ariaTarget:{type:Object,observer:"_ariaTargetChanged"},errorMessage:{type:String,observer:"_errorMessageChanged"},helperText:{type:String,observer:"_helperTextChanged"},accessibleName:{type:String,observer:"_accessibleNameChanged"},accessibleNameRef:{type:String,observer:"_accessibleNameRefChanged"}}}static get observers(){return["_invalidChanged(invalid)","_requiredChanged(required)"]}constructor(){super(),this._fieldAriaController=new Ln(this),this._helperController=new Fn(this),this._errorController=new Dn(this),this._errorController.addEventListener("slot-content-changed",t=>{this.toggleAttribute("has-error-message",t.detail.hasContent)}),this._labelController.addEventListener("slot-content-changed",t=>{const{hasContent:i,node:r}=t.detail;this.__labelChanged(i,r)}),this._helperController.addEventListener("slot-content-changed",t=>{const{hasContent:i,node:r}=t.detail;this.toggleAttribute("has-helper",i),this.__helperChanged(i,r)})}get _errorNode(){return this._errorController.node}get _helperNode(){return this._helperController.node}ready(){super.ready(),this.addController(this._fieldAriaController),this.addController(this._helperController),this.addController(this._errorController)}__helperChanged(t,i){t?this._fieldAriaController.setHelperId(i.id):this._fieldAriaController.setHelperId(null)}_accessibleNameChanged(t){this._fieldAriaController.setAriaLabel(t)}_accessibleNameRefChanged(t){this._fieldAriaController.setLabelId(t,!0)}__labelChanged(t,i){t?this._fieldAriaController.setLabelId(i.id):this._fieldAriaController.setLabelId(null)}_errorMessageChanged(t){this._errorController.setErrorMessage(t)}_helperTextChanged(t){this._helperController.setHelperText(t)}_ariaTargetChanged(t){t&&this._fieldAriaController.setTarget(t)}_requiredChanged(t){this._fieldAriaController.setRequired(t)}_invalidChanged(t){this._errorController.setInvalid(t),setTimeout(()=>{if(t){const i=this._errorNode;this._fieldAriaController.setErrorId(i&&i.id)}else this._fieldAriaController.setErrorId(null)})}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Vn=v(o=>class extends o{static get properties(){return{stateTarget:{type:Object,observer:"_stateTargetChanged"}}}static get delegateAttrs(){return[]}static get delegateProps(){return[]}ready(){super.ready(),this._createDelegateAttrsObserver(),this._createDelegatePropsObserver()}_stateTargetChanged(t){t&&(this._ensureAttrsDelegated(),this._ensurePropsDelegated())}_createDelegateAttrsObserver(){this._createMethodObserver(`_delegateAttrsChanged(${this.constructor.delegateAttrs.join(", ")})`)}_createDelegatePropsObserver(){this._createMethodObserver(`_delegatePropsChanged(${this.constructor.delegateProps.join(", ")})`)}_ensureAttrsDelegated(){this.constructor.delegateAttrs.forEach(t=>{this._delegateAttribute(t,this[t])})}_ensurePropsDelegated(){this.constructor.delegateProps.forEach(t=>{this._delegateProperty(t,this[t])})}_delegateAttrsChanged(...t){this.constructor.delegateAttrs.forEach((i,r)=>{this._delegateAttribute(i,t[r])})}_delegatePropsChanged(...t){this.constructor.delegateProps.forEach((i,r)=>{this._delegateProperty(i,t[r])})}_delegateAttribute(t,i){this.stateTarget&&(t==="invalid"&&this._delegateAttribute("aria-invalid",i?"true":!1),typeof i=="boolean"?this.stateTarget.toggleAttribute(t,i):i?this.stateTarget.setAttribute(t,i):this.stateTarget.removeAttribute(t))}_delegateProperty(t,i){this.stateTarget&&(this.stateTarget[t]=i)}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Un=v(o=>class extends Vn(ti(Zo(o))){static get constraints(){return["required"]}static get delegateAttrs(){return[...super.delegateAttrs,"required"]}ready(){super.ready(),this._createConstraintsObserver()}checkValidity(){return this.inputElement&&this._hasValidConstraints(this.constructor.constraints.map(t=>this[t]))?this.inputElement.checkValidity():!this.invalid}_hasValidConstraints(t){return t.some(i=>this.__isValidConstraint(i))}_createConstraintsObserver(){this._createMethodObserver(`_constraintsChanged(stateTarget, ${this.constructor.constraints.join(", ")})`)}_constraintsChanged(t,...i){if(!t)return;const r=this._hasValidConstraints(i),s=this.__previousHasConstraints&&!r;(this._hasValue||this.invalid)&&r?this.validate():s&&this._setInvalid(!1),this.__previousHasConstraints=r}_onChange(t){t.stopPropagation(),this.validate(),this.dispatchEvent(new CustomEvent("change",{detail:{sourceEvent:t},bubbles:t.bubbles,cancelable:t.cancelable}))}__isValidConstraint(t){return!!t||t===0}});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const jn=o=>class extends zn(Sn(Un(Bn(Mn(me(o)))))){static get properties(){return{allowedCharPattern:{type:String,observer:"_allowedCharPatternChanged"},autoselect:{type:Boolean,value:!1},name:{type:String,reflectToAttribute:!0},placeholder:{type:String,reflectToAttribute:!0},readonly:{type:Boolean,value:!1,reflectToAttribute:!0},title:{type:String,reflectToAttribute:!0}}}static get delegateAttrs(){return[...super.delegateAttrs,"name","type","placeholder","readonly","invalid","title"]}constructor(){super(),this._boundOnPaste=this._onPaste.bind(this),this._boundOnDrop=this._onDrop.bind(this),this._boundOnBeforeInput=this._onBeforeInput.bind(this)}get slotStyles(){return[`
          :is(input[slot='input'], textarea[slot='textarea'])::placeholder {
            font: inherit;
            color: inherit;
          }
        `]}_onFocus(t){super._onFocus(t),this.autoselect&&this.inputElement&&this.inputElement.select()}_onChange(t){t.stopPropagation(),this.validate(),this.dispatchEvent(new CustomEvent("change",{detail:{sourceEvent:t},bubbles:t.bubbles,cancelable:t.cancelable}))}_addInputListeners(t){super._addInputListeners(t),t.addEventListener("paste",this._boundOnPaste),t.addEventListener("drop",this._boundOnDrop),t.addEventListener("beforeinput",this._boundOnBeforeInput)}_removeInputListeners(t){super._removeInputListeners(t),t.removeEventListener("paste",this._boundOnPaste),t.removeEventListener("drop",this._boundOnDrop),t.removeEventListener("beforeinput",this._boundOnBeforeInput)}_onKeyDown(t){super._onKeyDown(t),this.allowedCharPattern&&!this.__shouldAcceptKey(t)&&(t.preventDefault(),this._markInputPrevented())}_markInputPrevented(){this.setAttribute("input-prevented",""),this._preventInputDebouncer=Y.debounce(this._preventInputDebouncer,rn.after(200),()=>{this.removeAttribute("input-prevented")})}__shouldAcceptKey(t){return t.metaKey||t.ctrlKey||!t.key||t.key.length!==1||this.__allowedCharRegExp.test(t.key)}_onPaste(t){if(this.allowedCharPattern){const i=t.clipboardData.getData("text");this.__allowedTextRegExp.test(i)||(t.preventDefault(),this._markInputPrevented())}}_onDrop(t){if(this.allowedCharPattern){const i=t.dataTransfer.getData("text");this.__allowedTextRegExp.test(i)||(t.preventDefault(),this._markInputPrevented())}}_onBeforeInput(t){this.allowedCharPattern&&t.data&&!this.__allowedTextRegExp.test(t.data)&&(t.preventDefault(),this._markInputPrevented())}_allowedCharPatternChanged(t){if(t)try{this.__allowedCharRegExp=new RegExp(`^${t}$`,"u"),this.__allowedTextRegExp=new RegExp(`^${t}*$`,"u")}catch(i){console.error(i)}}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const qn=o=>class extends jn(o){static get properties(){return{autocomplete:{type:String},autocorrect:{type:String},autocapitalize:{type:String,reflectToAttribute:!0}}}static get delegateAttrs(){return[...super.delegateAttrs,"autocapitalize","autocomplete","autocorrect"]}get __data(){return this.__dataValue||{}}set __data(t){this.__dataValue=t}_inputElementChanged(t){super._inputElementChanged(t),t&&(t.value&&t.value!==this.value&&(console.warn(`Please define value on the <${this.localName}> component!`),t.value=""),this.value&&(t.value=this.value))}_setFocused(t){super._setFocused(t),!t&&document.hasFocus()&&this.validate()}_onInput(t){super._onInput(t),this.invalid&&this.validate()}_valueChanged(t,i){super._valueChanged(t,i),i!==void 0&&this.invalid&&this.validate()}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Wn{constructor(e,t){this.input=e,this.__preventDuplicateLabelClick=this.__preventDuplicateLabelClick.bind(this),t.addEventListener("slot-content-changed",i=>{this.__initLabel(i.detail.node)}),this.__initLabel(t.node)}__initLabel(e){e&&(e.addEventListener("click",this.__preventDuplicateLabelClick),this.input&&e.setAttribute("for",this.input.id))}__preventDuplicateLabelClick(){const e=t=>{t.stopImmediatePropagation(),this.input.removeEventListener("click",e)};this.input.addEventListener("click",e)}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Yn=o=>class extends qn(o){static get properties(){return{maxlength:{type:Number},minlength:{type:Number},pattern:{type:String}}}static get delegateAttrs(){return[...super.delegateAttrs,"maxlength","minlength","pattern"]}static get constraints(){return[...super.constraints,"maxlength","minlength","pattern"]}constructor(){super(),this._setType("text")}get clearElement(){return this.$.clearButton}ready(){super.ready(),this.addController(new Tn(this,t=>{this._setInputElement(t),this._setFocusElement(t),this.stateTarget=t,this.ariaTarget=t})),this.addController(new Wn(this.inputElement,this._labelController))}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-text-field",Pn,{moduleId:"vaadin-text-field-styles"});class ve extends Yn(nt(lt(F))){static get is(){return"vaadin-text-field"}static get template(){return z`
      <style>
        [part='input-field'] {
          flex-grow: 0;
        }
      </style>

      <div class="vaadin-field-container">
        <div part="label">
          <slot name="label"></slot>
          <span part="required-indicator" aria-hidden="true" on-click="focus"></span>
        </div>

        <vaadin-input-container
          part="input-field"
          readonly="[[readonly]]"
          disabled="[[disabled]]"
          invalid="[[invalid]]"
          theme$="[[_theme]]"
        >
          <slot name="prefix" slot="prefix"></slot>
          <slot name="input"></slot>
          <slot name="suffix" slot="suffix"></slot>
          <div id="clearButton" part="clear-button" slot="suffix" aria-hidden="true"></div>
        </vaadin-input-container>

        <div part="helper-text">
          <slot name="helper"></slot>
        </div>

        <div part="error-message">
          <slot name="error-message"></slot>
        </div>
      </div>
      <slot name="tooltip"></slot>
    `}static get properties(){return{maxlength:{type:Number},minlength:{type:Number}}}ready(){super.ready(),this._tooltipController=new Go(this),this._tooltipController.setPosition("top"),this._tooltipController.setAriaTarget(this.inputElement),this.addController(this._tooltipController)}}P(ve);const ei=_`
  :host {
    /* Sizing */
    --lumo-button-size: var(--lumo-size-m);
    min-width: var(--vaadin-button-min-width, calc(var(--_button-size) * 2));
    height: var(--_button-size);
    padding: var(--vaadin-button-padding, 0 calc(var(--_button-size) / 3 + var(--lumo-border-radius-m) / 2));
    margin: var(--vaadin-button-margin, var(--lumo-space-xs) 0);
    box-sizing: border-box;
    /* Style */
    font-family: var(--lumo-font-family);
    font-size: var(--vaadin-button-font-size, var(--lumo-font-size-m));
    font-weight: var(--vaadin-button-font-weight, 500);
    color: var(--_lumo-button-text-color);
    background: var(--_lumo-button-background);
    border: var(--vaadin-button-border, none);
    border-radius: var(--vaadin-button-border-radius, var(--lumo-border-radius-m));
    cursor: var(--lumo-clickable-cursor);
    -webkit-tap-highlight-color: transparent;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    flex-shrink: 0;
    --_button-size: var(--vaadin-button-height, var(--lumo-button-size));
    --_focus-ring-color: var(--vaadin-focus-ring-color, var(--lumo-primary-color-50pct));
    --_focus-ring-width: var(--vaadin-focus-ring-width, 2px);
    /* Used by notification */
    --_lumo-button-background: var(--vaadin-button-background, var(--lumo-contrast-5pct));
    --_lumo-button-text-color: var(--vaadin-button-text-color, var(--lumo-primary-text-color));
    --_lumo-button-primary-background: var(--vaadin-button-primary-background, var(--lumo-primary-color));
    --_lumo-button-primary-text-color: var(--vaadin-button-primary-text-color, var(--lumo-primary-contrast-color));
  }

  /* Set only for the internal parts so we don't affect the host vertical alignment */
  [part='label'],
  [part='prefix'],
  [part='suffix'] {
    line-height: var(--lumo-line-height-xs);
  }

  [part='label'] {
    padding: calc(var(--lumo-button-size) / 6) 0;
  }

  :host([theme~='small']) {
    font-size: var(--lumo-font-size-s);
    --lumo-button-size: var(--lumo-size-s);
  }

  :host([theme~='large']) {
    font-size: var(--lumo-font-size-l);
    --lumo-button-size: var(--lumo-size-l);
  }

  /* For interaction states */
  :host::before,
  :host::after {
    content: '';
    /* We rely on the host always being relative */
    position: absolute;
    z-index: 1;
    inset: 0;
    background-color: currentColor;
    border-radius: inherit;
    opacity: 0;
    pointer-events: none;
  }

  /* Hover */

  @media (any-hover: hover) {
    :host(:hover)::before {
      opacity: 0.02;
    }
  }

  /* Active */

  :host::after {
    transition: opacity 1.4s, transform 0.1s;
    filter: blur(8px);
  }

  :host([active])::before {
    opacity: 0.05;
    transition-duration: 0s;
  }

  :host([active])::after {
    opacity: 0.1;
    transition-duration: 0s, 0s;
    transform: scale(0);
  }

  /* Keyboard focus */

  :host([focus-ring]) {
    box-shadow: 0 0 0 var(--_focus-ring-width) var(--_focus-ring-color);
  }

  :host([theme~='primary'][focus-ring]) {
    box-shadow: 0 0 0 1px var(--lumo-base-color), 0 0 0 3px var(--lumo-primary-color-50pct);
  }

  /* Types (primary, tertiary, tertiary-inline */

  :host([theme~='tertiary']),
  :host([theme~='tertiary-inline']) {
    --_background: transparent !important;
    background: var(--vaadin-button-tertiary-background, var(--_background));
    min-width: 0;
  }

  :host([theme~='tertiary']) {
    border: var(--vaadin-button-tertiary-border, none);
    color: var(--vaadin-button-tertiary-text-color, var(--lumo-primary-text-color));
    font-weight: var(--vaadin-button-tertiary-font-weight, 500);
    padding: var(--vaadin-button-tertiary-padding, 0 calc(var(--_button-size) / 6));
  }

  :host([theme~='tertiary-inline'])::before {
    display: none;
  }

  :host([theme~='tertiary-inline']) {
    margin: 0;
    height: auto;
    padding: 0;
    line-height: inherit;
    font-size: inherit;
  }

  :host([theme~='tertiary-inline']) [part='label'] {
    padding: 0;
    overflow: visible;
    line-height: inherit;
  }

  :host([theme~='primary']) {
    background: var(--_lumo-button-primary-background);
    border: var(--vaadin-button-primary-border, none);
    color: var(--_lumo-button-primary-text-color);
    font-weight: var(--vaadin-button-primary-font-weight, 600);
    min-width: calc(var(--lumo-button-size) * 2.5);
  }

  :host([theme~='primary'])::before {
    background-color: black;
  }

  @media (any-hover: hover) {
    :host([theme~='primary']:hover)::before {
      opacity: 0.05;
    }
  }

  :host([theme~='primary'][active])::before {
    opacity: 0.1;
  }

  :host([theme~='primary'][active])::after {
    opacity: 0.2;
  }

  /* Colors (success, error, contrast) */

  :host([theme~='success']) {
    color: var(--lumo-success-text-color);
  }

  :host([theme~='success'][theme~='primary']) {
    background-color: var(--lumo-success-color);
    color: var(--lumo-success-contrast-color);
  }

  :host([theme~='error']) {
    color: var(--lumo-error-text-color);
  }

  :host([theme~='error'][theme~='primary']) {
    background-color: var(--lumo-error-color);
    color: var(--lumo-error-contrast-color);
  }

  :host([theme~='contrast']) {
    color: var(--lumo-contrast);
  }

  :host([theme~='contrast'][theme~='primary']) {
    background-color: var(--lumo-contrast);
    color: var(--lumo-base-color);
  }

  /* Disabled state. Keep selectors after other color variants. */

  :host([disabled]) {
    pointer-events: none;
    color: var(--lumo-disabled-text-color);
  }

  :host([theme~='primary'][disabled]) {
    background-color: var(--lumo-contrast-30pct);
    color: var(--lumo-base-color);
  }

  :host([theme~='primary'][disabled]) [part] {
    opacity: 0.7;
  }

  /* Icons */

  [part] ::slotted(vaadin-icon) {
    display: inline-block;
    width: var(--lumo-icon-size-m);
    height: var(--lumo-icon-size-m);
  }

  /* Vaadin icons are based on a 16x16 grid (unlike Lumo and Material icons with 24x24), so they look too big by default */
  [part] ::slotted(vaadin-icon[icon^='vaadin:']) {
    padding: 0.25em;
    box-sizing: border-box !important;
  }

  [part='prefix'] {
    margin-left: -0.25em;
    margin-right: 0.25em;
  }

  [part='suffix'] {
    margin-left: 0.25em;
    margin-right: -0.25em;
  }

  /* Icon-only */

  :host([theme~='icon']:not([theme~='tertiary-inline'])) {
    min-width: var(--lumo-button-size);
    padding-left: calc(var(--lumo-button-size) / 4);
    padding-right: calc(var(--lumo-button-size) / 4);
  }

  :host([theme~='icon']) [part='prefix'],
  :host([theme~='icon']) [part='suffix'] {
    margin-left: 0;
    margin-right: 0;
  }

  /* RTL specific styles */

  :host([dir='rtl']) [part='prefix'] {
    margin-left: 0.25em;
    margin-right: -0.25em;
  }

  :host([dir='rtl']) [part='suffix'] {
    margin-left: -0.25em;
    margin-right: 0.25em;
  }

  :host([dir='rtl'][theme~='icon']) [part='prefix'],
  :host([dir='rtl'][theme~='icon']) [part='suffix'] {
    margin-left: 0;
    margin-right: 0;
  }
`;g("vaadin-button",ei,{moduleId:"lumo-button"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Gn=_`
  :host {
    display: inline-block;
    position: relative;
    outline: none;
    white-space: nowrap;
    -webkit-user-select: none;
    -moz-user-select: none;
    user-select: none;
  }

  :host([hidden]) {
    display: none !important;
  }

  /* Aligns the button with form fields when placed on the same line.
  Note, to make it work, the form fields should have the same "::before" pseudo-element. */
  .vaadin-button-container::before {
    content: '\\2003';
    display: inline-block;
    width: 0;
    max-height: 100%;
  }

  .vaadin-button-container {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    width: 100%;
    height: 100%;
    min-height: inherit;
    text-shadow: inherit;
  }

  [part='prefix'],
  [part='suffix'] {
    flex: none;
  }

  [part='label'] {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  @media (forced-colors: active) {
    :host {
      outline: 1px solid;
      outline-offset: -1px;
    }

    :host([focused]) {
      outline-width: 2px;
    }

    :host([disabled]) {
      outline-color: GrayText;
    }
  }
`,$n=o=>o`
  <div class="vaadin-button-container">
    <span part="prefix" aria-hidden="true">
      <slot name="prefix"></slot>
    </span>
    <span part="label">
      <slot></slot>
    </span>
    <span part="suffix" aria-hidden="true">
      <slot name="suffix"></slot>
    </span>
  </div>
  <slot name="tooltip"></slot>
`;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const Kn=o=>o,oi=typeof document.head.style.touchAction=="string",Ct="__polymerGestures",Ut="__polymerGesturesHandled",ie="__polymerGesturesTouchAction",Ge=25,$e=5,Jn=2,Zn=["mousedown","mousemove","mouseup","click"],Xn=[0,1,4,2],Qn=function(){try{return new MouseEvent("test",{buttons:1}).buttons===1}catch{return!1}}();function be(o){return Zn.indexOf(o)>-1}let ta=!1;(function(){try{const o=Object.defineProperty({},"passive",{get(){ta=!0}});window.addEventListener("test",null,o),window.removeEventListener("test",null,o)}catch{}})();function ii(o){be(o)}const ea=navigator.userAgent.match(/iP(?:[oa]d|hone)|Android/u),oa={button:!0,command:!0,fieldset:!0,input:!0,keygen:!0,optgroup:!0,option:!0,select:!0,textarea:!0};function L(o){const e=o.type;if(!be(e))return!1;if(e==="mousemove"){let i=o.buttons===void 0?1:o.buttons;return o instanceof window.MouseEvent&&!Qn&&(i=Xn[o.which]||0),!!(i&1)}return(o.button===void 0?0:o.button)===0}function ia(o){if(o.type==="click"){if(o.detail===0)return!0;const e=k(o);if(!e.nodeType||e.nodeType!==Node.ELEMENT_NODE)return!0;const t=e.getBoundingClientRect(),i=o.pageX,r=o.pageY;return!(i>=t.left&&i<=t.right&&r>=t.top&&r<=t.bottom)}return!1}const S={touch:{x:0,y:0,id:-1,scrollDecided:!1}};function ra(o){let e="auto";const t=si(o);for(let i=0,r;i<t.length;i++)if(r=t[i],r[ie]){e=r[ie];break}return e}function ri(o,e,t){o.movefn=e,o.upfn=t,document.addEventListener("mousemove",e),document.addEventListener("mouseup",t)}function q(o){document.removeEventListener("mousemove",o.movefn),document.removeEventListener("mouseup",o.upfn),o.movefn=null,o.upfn=null}const si=window.ShadyDOM&&window.ShadyDOM.noPatch?window.ShadyDOM.composedPath:o=>o.composedPath&&o.composedPath()||[],ct={},N=[];function sa(o,e){let t=document.elementFromPoint(o,e),i=t;for(;i&&i.shadowRoot&&!window.ShadyDOM;){const r=i;if(i=i.shadowRoot.elementFromPoint(o,e),r===i)break;i&&(t=i)}return t}function k(o){const e=si(o);return e.length>0?e[0]:o.target}function ni(o){const e=o.type,i=o.currentTarget[Ct];if(!i)return;const r=i[e];if(!r)return;if(!o[Ut]&&(o[Ut]={},e.startsWith("touch"))){const n=o.changedTouches[0];if(e==="touchstart"&&o.touches.length===1&&(S.touch.id=n.identifier),S.touch.id!==n.identifier)return;oi||(e==="touchstart"||e==="touchmove")&&na(o)}const s=o[Ut];if(!s.skip){for(let n=0,a;n<N.length;n++)a=N[n],r[a.name]&&!s[a.name]&&a.flow&&a.flow.start.indexOf(o.type)>-1&&a.reset&&a.reset();for(let n=0,a;n<N.length;n++)a=N[n],r[a.name]&&!s[a.name]&&(s[a.name]=!0,a[e](o))}}function na(o){const e=o.changedTouches[0],t=o.type;if(t==="touchstart")S.touch.x=e.clientX,S.touch.y=e.clientY,S.touch.scrollDecided=!1;else if(t==="touchmove"){if(S.touch.scrollDecided)return;S.touch.scrollDecided=!0;const i=ra(o);let r=!1;const s=Math.abs(S.touch.x-e.clientX),n=Math.abs(S.touch.y-e.clientY);o.cancelable&&(i==="none"?r=!0:i==="pan-x"?r=n>s:i==="pan-y"&&(r=s>n)),r?o.preventDefault():At("track")}}function Ke(o,e,t){return ct[e]?(aa(o,e,t),!0):!1}function _l(o,e,t){return ct[e]?(la(o,e,t),!0):!1}function aa(o,e,t){const i=ct[e],r=i.deps,s=i.name;let n=o[Ct];n||(o[Ct]=n={});for(let a=0,l,d;a<r.length;a++)l=r[a],!(ea&&be(l)&&l!=="click")&&(d=n[l],d||(n[l]=d={_count:0}),d._count===0&&o.addEventListener(l,ni,ii(l)),d[s]=(d[s]||0)+1,d._count=(d._count||0)+1);o.addEventListener(e,t),i.touchAction&&ca(o,i.touchAction)}function la(o,e,t){const i=ct[e],r=i.deps,s=i.name,n=o[Ct];if(n)for(let a=0,l,d;a<r.length;a++)l=r[a],d=n[l],d&&d[s]&&(d[s]=(d[s]||1)-1,d._count=(d._count||1)-1,d._count===0&&o.removeEventListener(l,ni,ii(l)));o.removeEventListener(e,t)}function ye(o){N.push(o),o.emits.forEach(e=>{ct[e]=o})}function da(o){for(let e=0,t;e<N.length;e++){t=N[e];for(let i=0,r;i<t.emits.length;i++)if(r=t.emits[i],r===o)return t}return null}function ca(o,e){oi&&o instanceof HTMLElement&&Ho.run(()=>{o.style.touchAction=e}),o[ie]=e}function xe(o,e,t){const i=new Event(e,{bubbles:!0,cancelable:!0,composed:!0});if(i.detail=t,Kn(o).dispatchEvent(i),i.defaultPrevented){const r=t.preventer||t.sourceEvent;r&&r.preventDefault&&r.preventDefault()}}function At(o){const e=da(o);e.info&&(e.info.prevent=!0)}ye({name:"downup",deps:["mousedown","touchstart","touchend"],flow:{start:["mousedown","touchstart"],end:["mouseup","touchend"]},emits:["down","up"],info:{movefn:null,upfn:null},reset(){q(this.info)},mousedown(o){if(!L(o))return;const e=k(o),t=this,i=s=>{L(s)||(Q("up",e,s),q(t.info))},r=s=>{L(s)&&Q("up",e,s),q(t.info)};ri(this.info,i,r),Q("down",e,o)},touchstart(o){Q("down",k(o),o.changedTouches[0],o)},touchend(o){Q("up",k(o),o.changedTouches[0],o)}});function Q(o,e,t,i){e&&xe(e,o,{x:t.clientX,y:t.clientY,sourceEvent:t,preventer:i,prevent(r){return At(r)}})}ye({name:"track",touchAction:"none",deps:["mousedown","touchstart","touchmove","touchend"],flow:{start:["mousedown","touchstart"],end:["mouseup","touchend"]},emits:["track"],info:{x:0,y:0,state:"start",started:!1,moves:[],addMove(o){this.moves.length>Jn&&this.moves.shift(),this.moves.push(o)},movefn:null,upfn:null,prevent:!1},reset(){this.info.state="start",this.info.started=!1,this.info.moves=[],this.info.x=0,this.info.y=0,this.info.prevent=!1,q(this.info)},mousedown(o){if(!L(o))return;const e=k(o),t=this,i=s=>{const n=s.clientX,a=s.clientY;Je(t.info,n,a)&&(t.info.state=t.info.started?s.type==="mouseup"?"end":"track":"start",t.info.state==="start"&&At("tap"),t.info.addMove({x:n,y:a}),L(s)||(t.info.state="end",q(t.info)),e&&jt(t.info,e,s),t.info.started=!0)},r=s=>{t.info.started&&i(s),q(t.info)};ri(this.info,i,r),this.info.x=o.clientX,this.info.y=o.clientY},touchstart(o){const e=o.changedTouches[0];this.info.x=e.clientX,this.info.y=e.clientY},touchmove(o){const e=k(o),t=o.changedTouches[0],i=t.clientX,r=t.clientY;Je(this.info,i,r)&&(this.info.state==="start"&&At("tap"),this.info.addMove({x:i,y:r}),jt(this.info,e,t),this.info.state="track",this.info.started=!0)},touchend(o){const e=k(o),t=o.changedTouches[0];this.info.started&&(this.info.state="end",this.info.addMove({x:t.clientX,y:t.clientY}),jt(this.info,e,t))}});function Je(o,e,t){if(o.prevent)return!1;if(o.started)return!0;const i=Math.abs(o.x-e),r=Math.abs(o.y-t);return i>=$e||r>=$e}function jt(o,e,t){if(!e)return;const i=o.moves[o.moves.length-2],r=o.moves[o.moves.length-1],s=r.x-o.x,n=r.y-o.y;let a,l=0;i&&(a=r.x-i.x,l=r.y-i.y),xe(e,"track",{state:o.state,x:t.clientX,y:t.clientY,dx:s,dy:n,ddx:a,ddy:l,sourceEvent:t,hover(){return sa(t.clientX,t.clientY)}})}ye({name:"tap",deps:["mousedown","click","touchstart","touchend"],flow:{start:["mousedown","touchstart"],end:["click","touchend"]},emits:["tap"],info:{x:NaN,y:NaN,prevent:!1},reset(){this.info.x=NaN,this.info.y=NaN,this.info.prevent=!1},mousedown(o){L(o)&&(this.info.x=o.clientX,this.info.y=o.clientY)},click(o){L(o)&&Ze(this.info,o)},touchstart(o){const e=o.changedTouches[0];this.info.x=e.clientX,this.info.y=e.clientY},touchend(o){Ze(this.info,o.changedTouches[0],o)}});function Ze(o,e,t){const i=Math.abs(e.clientX-o.x),r=Math.abs(e.clientY-o.y),s=k(t||e);!s||oa[s.localName]&&s.hasAttribute("disabled")||(isNaN(i)||isNaN(r)||i<=Ge&&r<=Ge||ia(e))&&(o.prevent||xe(s,"tap",{x:e.clientX,y:e.clientY,sourceEvent:e,preventer:t}))}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ua=o=>class extends Ko(me(o)){get _activeKeys(){return[" "]}ready(){super.ready(),Ke(this,"down",t=>{this._shouldSetActive(t)&&this._setActive(!0)}),Ke(this,"up",()=>{this._setActive(!1)})}disconnectedCallback(){super.disconnectedCallback(),this._setActive(!1)}_shouldSetActive(t){return!this.disabled}_onKeyDown(t){super._onKeyDown(t),this._shouldSetActive(t)&&this._activeKeys.includes(t.key)&&(this._setActive(!0),document.addEventListener("keyup",i=>{this._activeKeys.includes(i.key)&&this._setActive(!1)},{once:!0}))}_setActive(t){this.toggleAttribute("active",t)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ha=o=>class extends ua(Jo($o(o))){static get properties(){return{tabindex:{type:Number,value:0,reflectToAttribute:!0}}}get _activeKeys(){return["Enter"," "]}ready(){super.ready(),this.hasAttribute("role")||this.setAttribute("role","button")}_onKeyDown(t){super._onKeyDown(t),!(t.altKey||t.shiftKey||t.ctrlKey||t.metaKey)&&this._activeKeys.includes(t.key)&&(t.preventDefault(),this.click())}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-button",Gn,{moduleId:"vaadin-button-styles"});class ai extends ha(lt(nt(at(F)))){static get is(){return"vaadin-button"}static get template(){return $n(z)}ready(){super.ready(),this._tooltipController=new Go(this),this.addController(this._tooltipController)}}P(ai);function pa({currentTarget:o}){o.disabled=o.hasAttribute("disableOnClick")}window.Vaadin.Flow.button={initDisableOnClick:o=>{o.__hasDisableOnClickListener||(o.addEventListener("click",pa),o.__hasDisableOnClickListener=!0)}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const fa=_`
  :host {
    position: absolute;
    right: 0;
    top: 0;
    margin: 0;
    padding: 0;
    width: 100%;
    height: 100%;
    min-width: auto;
    background: transparent;
    outline: none;
  }
`;g("vaadin-password-field-button",[ei,fa],{moduleId:"lumo-password-field-button"});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ma=_`
  [part='reveal-button']::before {
    content: var(--lumo-icons-eye);
  }

  :host([password-visible]) [part='reveal-button']::before {
    content: var(--lumo-icons-eye-disabled);
  }

  /* Make it easy to hide the button across the whole app */
  [part='reveal-button'] {
    position: relative;
    display: var(--lumo-password-field-reveal-button-display, block);
  }

  [part='reveal-button'][hidden] {
    display: none !important;
  }
`;g("vaadin-password-field",[St,ma],{moduleId:"lumo-password-field"});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class _a extends ai{static get is(){return"vaadin-password-field-button"}static get template(){return z`
      <style>
        :host {
          display: block;
        }

        :host([hidden]) {
          display: none !important;
        }
      </style>
      <slot name="tooltip"></slot>
    `}}P(_a);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ga=z`
  <div part="reveal-button" slot="suffix">
    <slot name="reveal"></slot>
  </div>
`;let mt;class va extends ve{static get is(){return"vaadin-password-field"}static get template(){if(!mt){mt=super.template.cloneNode(!0);const e=ga.content.querySelector('[part="reveal-button"]');mt.content.querySelector('[part="input-field"]').appendChild(e)}return mt}static get properties(){return{revealButtonHidden:{type:Boolean,observer:"_revealButtonHiddenChanged",value:!1},passwordVisible:{type:Boolean,value:!1,reflectToAttribute:!0,observer:"_passwordVisibleChanged",readOnly:!0},i18n:{type:Object,value:()=>({reveal:"Show password"})}}}static get observers(){return["__i18nChanged(i18n.*)"]}constructor(){super(),this._setType("password"),this.__boundRevealButtonClick=this._onRevealButtonClick.bind(this),this.__boundRevealButtonMouseDown=this._onRevealButtonMouseDown.bind(this),this.__lastChange=""}get slotStyles(){const e=this.localName;return[...super.slotStyles,`
        ${e} [slot="input"]::-ms-reveal {
          display: none;
        }
      `]}get _revealNode(){return this._revealButtonController&&this._revealButtonController.node}ready(){super.ready(),this._revealPart=this.shadowRoot.querySelector('[part="reveal-button"]'),this._revealButtonController=new dt(this,"reveal","vaadin-password-field-button",{initializer:e=>{e.disabled=this.disabled,e.addEventListener("click",this.__boundRevealButtonClick),e.addEventListener("mousedown",this.__boundRevealButtonMouseDown)}}),this.addController(this._revealButtonController),this.__updateAriaLabel(this.i18n),this._updateToggleState(!1),this._toggleRevealHidden(this.revealButtonHidden),this.inputElement&&(this.inputElement.autocapitalize="off")}_onChange(e){super._onChange(e),this.__lastChange=this.inputElement.value}_shouldSetFocus(e){return e.target===this.inputElement||e.target===this._revealNode}_shouldRemoveFocus(e){return!(e.relatedTarget===this._revealNode||e.relatedTarget===this.inputElement&&e.target===this._revealNode)}_setFocused(e){if(super._setFocused(e),!e)this._setPasswordVisible(!1),this.__lastChange!==this.inputElement.value&&(this.__lastChange=this.inputElement.value,this.dispatchEvent(new CustomEvent("change",{bubbles:!0})));else{const t=this.getRootNode().activeElement===this._revealNode;this.toggleAttribute("focus-ring",this._keyboardActive&&!t)}}__updateAriaLabel(e){e.reveal&&this._revealNode&&this._revealNode.setAttribute("aria-label",e.reveal)}__i18nChanged(e){this.__updateAriaLabel(e.base)}_revealButtonHiddenChanged(e){this._toggleRevealHidden(e)}_togglePasswordVisibility(){this._setPasswordVisible(!this.passwordVisible)}_onRevealButtonClick(){this._togglePasswordVisibility()}_onRevealButtonMouseDown(e){e.preventDefault(),this.inputElement.focus()}_toggleRevealHidden(e){this._revealNode&&(e?(this._revealPart.setAttribute("hidden",""),this._revealNode.setAttribute("tabindex","-1"),this._revealNode.setAttribute("aria-hidden","true")):(this._revealPart.removeAttribute("hidden"),this._revealNode.setAttribute("tabindex","0"),this._revealNode.removeAttribute("aria-hidden")))}_updateToggleState(e){this._revealNode&&this._revealNode.setAttribute("aria-pressed",e?"true":"false")}_passwordVisibleChanged(e){this._setType(e?"text":"password"),this._updateToggleState(e)}_disabledChanged(e,t){super._disabledChanged(e,t),this._revealNode&&(this._revealNode.disabled=e)}}P(va);const ba=_`
  :host([theme~='margin']) {
    margin: var(--lumo-space-m);
  }

  :host([theme~='padding']) {
    padding: var(--lumo-space-m);
  }

  :host([theme~='spacing-xs']) {
    gap: var(--lumo-space-xs);
  }

  :host([theme~='spacing-s']) {
    gap: var(--lumo-space-s);
  }

  :host([theme~='spacing']) {
    gap: var(--lumo-space-m);
  }

  :host([theme~='spacing-l']) {
    gap: var(--lumo-space-l);
  }

  :host([theme~='spacing-xl']) {
    gap: var(--lumo-space-xl);
  }
`;g("vaadin-vertical-layout",ba,{moduleId:"lumo-vertical-layout"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class ya extends lt(nt(F)){static get template(){return z`
      <style>
        :host {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          box-sizing: border-box;
        }

        :host([hidden]) {
          display: none !important;
        }

        /* Theme variations */
        :host([theme~='margin']) {
          margin: 1em;
        }

        :host([theme~='padding']) {
          padding: 1em;
        }

        :host([theme~='spacing']) {
          gap: 1em;
        }
      </style>

      <slot></slot>
    `}static get is(){return"vaadin-vertical-layout"}}P(ya);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const xa=_`
  :host([dir='rtl']) [part='input-field'] ::slotted(input) {
    --_lumo-text-field-overflow-mask-image: linear-gradient(to left, transparent, #000 1.25em);
  }

  :host([dir='rtl']) [part='input-field'] ::slotted(input:placeholder-shown) {
    --_lumo-text-field-overflow-mask-image: none;
  }
`;g("vaadin-email-field",[St,xa],{moduleId:"lumo-email-field"});/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const wa=_`
  :host([dir='rtl']) [part='input-field'] {
    direction: ltr;
  }

  :host([dir='rtl']) [part='input-field'] ::slotted(input)::placeholder {
    direction: rtl;
    text-align: left;
  }
`;/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */g("vaadin-email-field",wa,{moduleId:"vaadin-email-field-styles"});class Ca extends ve{static get is(){return"vaadin-email-field"}constructor(){super(),this._setType("email"),this.pattern="^([a-zA-Z0-9_\\.\\-+])+@[a-zA-Z0-9\\-.]+\\.[a-zA-Z0-9\\-]{2,}$"}ready(){super.ready(),this.inputElement&&(this.inputElement.autocapitalize="off")}}P(Ca);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */O("color",no);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Aa=_`
  ${oo(ao.cssText.replace(/,\s*:host/su,""))}
`;O("typography",Aa);/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */class re extends hi{constructor(e){if(super(e),this.et=qt,e.type!==pi.CHILD)throw Error(this.constructor.directiveName+"() can only be used in child bindings")}render(e){if(e===qt||e==null)return this.vt=void 0,this.et=e;if(e===fi)return e;if(typeof e!="string")throw Error(this.constructor.directiveName+"() called with a non-string value");if(e===this.et)return this.vt;this.et=e;const t=[e];return t.raw=t,this.vt={_$litType$:this.constructor.resultType,strings:t,values:[]}}}re.directiveName="unsafeHTML",re.resultType=1;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */class se extends re{}se.directiveName="unsafeSVG",se.resultType=2;const Ea=mi(se);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Xe(o){let e=qt;if(o){const t=o.cloneNode(!0);t.removeAttribute("id"),e=_i`${Ea(t.outerHTML)}`}return e}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const M={},Qe=new Set;function li(o,e){return(o||"").replace(`${e}:`,"")}function to(o){return o?o.split(":")[0]||"vaadin":void 0}function eo(o,e){o._icons=[...o.querySelectorAll("[id]")].reduce((t,i)=>{const r=li(i.id,e);return t[r]=i,t},{})}class di extends lt(F){static get template(){return null}static get is(){return"vaadin-iconset"}static get properties(){return{name:{type:String,observer:"__nameChanged"},size:{type:Number,value:24}}}static get attachedIcons(){return Qe}static getIconset(e){return M[e]}static getIconSvg(e,t){const i=t||to(e),r=this.getIconset(i);if(!e||!r)return{svg:Xe(null)};const s=li(e,i),n=r._icons[s];return{preserveAspectRatio:n?n.getAttribute("preserveAspectRatio"):null,svg:Xe(n),size:r.size,viewBox:n?n.getAttribute("viewBox"):null}}static register(e,t,i){if(!M[e]){const r=document.createElement("vaadin-iconset");r.appendChild(i.content.cloneNode(!0)),M[e]=r,eo(r,e),r.size=t,r.name=e,r.__nameChanged(e)}}connectedCallback(){super.connectedCallback(),this.style.display="none";const{name:e}=this;M[e]=this,eo(this,e),this.__updateIcons(e)}__updateIcons(e){Qe.forEach(t=>{e===to(t.icon)&&t._applyIcon()})}__nameChanged(e,t){t&&(M[e]=M[t],delete M[t]),e&&this.__updateIcons(e)}}P(di);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ci=document.createElement("template");ci.innerHTML=`<svg xmlns="http://www.w3.org/2000/svg"><defs>
<g id="lumo:align-center"><path d="M167 217c0-18 17-33 38-34H795c21 0 38 15 38 34 0 18-17 33-38 33H205C184 250 167 235 167 217z m83 191c0-18 13-33 29-33H721c16 0 29 15 29 33 0 18-13 33-29 34H279C263 442 250 427 250 408zM250 792c0-18 13-33 29-34H721c16 0 29 15 29 34s-13 33-29 33H279C263 825 250 810 250 792z m-83-192c0-18 17-33 38-33H795c21 0 38 15 38 33s-17 33-38 33H205C184 633 167 618 167 600z" fill-rule="evenodd" clip-rule="evenodd"></path></g>
<g id="lumo:align-left"><path d="M167 217c0-18 17-33 38-34H795c21 0 38 15 38 34 0 18-17 33-38 33H205C184 250 167 235 167 217z m0 191c0-18 13-33 28-33H638c16 0 29 15 29 33 0 18-13 33-29 34H195C179 442 167 427 167 408zM167 792c0-18 13-33 28-34H638c16 0 29 15 29 34s-13 33-29 33H195C179 825 167 810 167 792z m0-192c0-18 17-33 38-33H795c21 0 38 15 38 33s-17 33-38 33H205C184 633 167 618 167 600z" fill-rule="evenodd" clip-rule="evenodd"></path></g>
<g id="lumo:align-right"><path d="M167 217c0-18 17-33 38-34H795c21 0 38 15 38 34 0 18-17 33-38 33H205C184 250 167 235 167 217z m166 191c0-18 13-33 29-33H805c16 0 29 15 28 33 0 18-13 33-28 34H362C346 442 333 427 333 408zM333 792c0-18 13-33 29-34H805c16 0 29 15 28 34s-13 33-28 33H362C346 825 333 810 333 792z m-166-192c0-18 17-33 38-33H795c21 0 38 15 38 33s-17 33-38 33H205C184 633 167 618 167 600z" fill-rule="evenodd" clip-rule="evenodd"></path></g>
<g id="lumo:angle-down"><path d="M283 391c-18-16-46-15-63 4-16 18-15 46 3 63l244 224c17 15 43 15 60 0l250-229c18-16 20-45 3-63-16-18-45-20-63-4l-220 203-214-198z"></path></g>
<g id="lumo:angle-left"><path d="M601 710c16 18 15 46-3 63-18 16-46 15-63-4l-224-244c-15-17-15-43 0-59l229-250c16-18 45-20 63-4 18 16 20 45 3 63l-203 220 198 215z"></path></g>
<g id="lumo:angle-right"><path d="M399 275c-16-18-15-46 3-63 18-16 46-15 63 4l224 244c15 17 15 43 0 59l-229 250c-16 18-45 20-63 4-18-16-20-45-3-63l203-220-198-215z"></path></g>
<g id="lumo:angle-up"><path d="M283 635c-18 16-46 15-63-3-16-18-15-46 3-63l244-224c17-15 43-15 60 0l250 229c18 16 20 45 3 63-16 18-45 20-63 3l-220-202L283 635z"></path></g>
<g id="lumo:arrow-down"><path d="M538 646l125-112c15-14 39-12 53 4 14 15 12 39-4 53l-187 166c0 0 0 0 0 0-14 13-36 12-50 0l-187-166c-15-14-17-37-4-53 14-15 37-17 53-4L462 646V312c0-21 17-38 38-37s38 17 37 37v334z"></path></g>
<g id="lumo:arrow-left"><path d="M375 538l111 125c14 15 12 39-3 53-15 14-39 12-53-4l-166-187c0 0 0 0 0 0-13-14-12-36 0-50l166-187c14-15 37-17 53-4 15 14 17 37 3 53L375 463h333c21 0 38 17 38 37 0 21-17 38-38 38h-333z"></path></g>
<g id="lumo:arrow-right"><path d="M625 538h-333c-21 0-38-17-38-38 0-21 17-38 38-37h333l-111-126c-14-15-12-39 3-53 15-14 39-12 53 4l166 187c13 14 13 36 0 50 0 0 0 0 0 0l-166 187c-14 15-37 17-53 4-15-14-17-37-3-53l111-125z"></path></g>
<g id="lumo:arrow-up"><path d="M538 354V688c0 21-17 38-38 37s-38-17-38-38V354l-125 112c-15 14-39 12-53-4-14-15-12-39 4-53l187-166c14-13 36-13 50 0 0 0 0 0 0 0l187 166c15 14 17 37 4 53-14 15-37 17-53 4L538 354z"></path></g>
<g id="lumo:bar-chart"><path d="M175 500h108c28 0 50 22 50 50v233c0 28-22 50-50 50H175c-28 0-50-22-50-50v-233c0-28 22-50 50-50z m33 67c-9 0-17 7-16 16v167c0 9 7 17 16 17h42c9 0 17-7 17-17v-167c0-9-7-17-17-16H208zM446 167h108c28 0 50 22 50 50v566c0 28-22 50-50 50h-108c-28 0-50-22-50-50V217c0-28 22-50 50-50z m33 66c-9 0-17 7-17 17v500c0 9 7 17 17 17h42c9 0 17-7 16-17V250c0-9-7-17-16-17h-42zM717 333h108c28 0 50 22 50 50v400c0 28-22 50-50 50h-108c-28 0-50-22-50-50V383c0-28 22-50 50-50z m33 67c-9 0-17 7-17 17v333c0 9 7 17 17 17h42c9 0 17-7 16-17v-333c0-9-7-17-16-17h-42z"></path></g>
<g id="lumo:bell"><path d="M367 675H292v-258C292 325 366 250 459 250H458V208c0-23 18-42 42-41 23 0 42 18 42 41v42h-1C634 250 708 325 708 417V675h-75v-258c0-51-41-92-91-92h-84C408 325 367 366 367 417V675z m-159 37c0-21 17-38 38-37h508c21 0 37 17 38 37 0 21-17 38-38 38H246C225 750 208 733 208 713z m230 71h125v32c0 17-14 31-32 31h-62c-17 0-32-14-31-31v-32z"></path></g>
<g id="lumo:calendar"><path d="M375 208h250v-20C625 176 634 167 646 167h41C699 167 708 176 708 188V208h74c23 0 41 19 41 42v42C823 315 804 333 782 333H218C196 333 177 315 177 292V250C177 227 196 208 218 208H292v-20C292 176 301 167 313 167h41C366 167 375 176 375 188V208zM229 375h42C283 375 292 384 292 396v41C292 449 282 458 271 458h-42C217 458 208 449 208 437v-41C208 384 218 375 229 375z m125 0h42C408 375 417 384 417 396v41C417 449 407 458 396 458h-42C342 458 333 449 333 437v-41C333 384 343 375 354 375z m125 0h42C533 375 542 384 542 396v41C542 449 532 458 521 458h-42C467 458 458 449 458 437v-41C458 384 468 375 479 375z m-250 125h42C283 500 292 509 292 521v41C292 574 282 583 271 583h-42C217 583 208 574 208 562v-41C208 509 218 500 229 500z m125 0h42C408 500 417 509 417 521v41C417 574 407 583 396 583h-42C342 583 333 574 333 562v-41C333 509 343 500 354 500z m125 0h42c12 0 21 9 21 21v41C542 574 532 583 521 583h-42C467 583 458 574 458 562v-41C458 509 468 500 479 500z m-250 125h42C283 625 292 634 292 646v41C292 699 282 708 271 708h-42C217 708 208 699 208 687v-41C208 634 218 625 229 625z m125 0h42C408 625 417 634 417 646v41C417 699 407 708 396 708h-42C342 708 333 699 333 687v-41C333 634 343 625 354 625z m125 0h42c12 0 21 9 21 21v41C542 699 532 708 521 708h-42C467 708 458 699 458 687v-41C458 634 468 625 479 625z m125-250h42C658 375 667 384 667 396v41C667 449 657 458 646 458h-42C592 458 583 449 583 437v-41C583 384 593 375 604 375z m0 125h42c12 0 21 9 21 21v41C667 574 657 583 646 583h-42C592 583 583 574 583 562v-41C583 509 593 500 604 500z m0 125h42c12 0 21 9 21 21v41C667 699 657 708 646 708h-42C592 708 583 699 583 687v-41C583 634 593 625 604 625z m125 0h42c12 0 21 9 21 21v41C792 699 782 708 771 708h-42C717 708 708 699 708 687v-41C708 634 718 625 729 625z m-500 125h42C283 750 292 759 292 771v41C292 824 282 833 271 833h-42C217 833 208 824 208 812v-41C208 759 218 750 229 750z m125 0h42C408 750 417 759 417 771v41C417 824 407 833 396 833h-42C342 833 333 824 333 812v-41C333 759 343 750 354 750z m125 0h42c12 0 21 9 21 21v41C542 824 532 833 521 833h-42C467 833 458 824 458 812v-41C458 759 468 750 479 750z m125 0h42c12 0 21 9 21 21v41C667 824 657 833 646 833h-42C592 833 583 824 583 812v-41C583 759 593 750 604 750z m125 0h42c12 0 21 9 21 21v41C792 824 782 833 771 833h-42C717 833 708 824 708 812v-41C708 759 718 750 729 750z m0-250h42c12 0 21 9 21 21v41C792 574 782 583 771 583h-42C717 583 708 574 708 562v-41C708 509 718 500 729 500z m0-125h42C783 375 792 384 792 396v41C792 449 782 458 771 458h-42C717 458 708 449 708 437v-41C708 384 718 375 729 375z"></path></g>
<g id="lumo:checkmark"><path d="M318 493c-15-15-38-15-53 0-15 15-15 38 0 53l136 136c15 15 38 15 53 0l323-322c15-15 15-38 0-53-15-15-38-15-54 0l-295 296-110-110z"></path></g>
<g id="lumo:chevron-down"><path d="M533 654l210-199c9-9 9-23 0-32C739 419 733 417 726 417H274C261 417 250 427 250 439c0 6 2 12 7 16l210 199c18 17 48 17 66 0z"></path></g>
<g id="lumo:chevron-left"><path d="M346 533l199 210c9 9 23 9 32 0 4-4 7-10 6-17V274C583 261 573 250 561 250c-6 0-12 2-16 7l-199 210c-17 18-17 48 0 66z"></path></g>
<g id="lumo:chevron-right"><path d="M654 533L455 743c-9 9-23 9-32 0C419 739 417 733 417 726V274C417 261 427 250 439 250c6 0 12 2 16 7l199 210c17 18 17 48 0 66z"></path></g>
<g id="lumo:chevron-up"><path d="M533 346l210 199c9 9 9 23 0 32-4 4-10 7-17 6H274C261 583 250 573 250 561c0-6 2-12 7-16l210-199c18-17 48-17 66 0z"></path></g>
<g id="lumo:clock"><path d="M538 489l85 85c15 15 15 38 0 53-15 15-38 15-53 0l-93-93a38 38 0 0 1-2-2C467 525 462 515 462 504V308c0-21 17-38 38-37 21 0 38 17 37 37v181zM500 833c-184 0-333-149-333-333s149-333 333-333 333 149 333 333-149 333-333 333z m0-68c146 0 265-118 265-265 0-146-118-265-265-265-146 0-265 118-265 265 0 146 118 265 265 265z"></path></g>
<g id="lumo:cog"><path d="M833 458l-81-18c-8-25-17-50-29-75L767 292 708 233l-72 49c-21-12-46-25-75-30L542 167h-84l-19 79c-25 8-50 17-71 30L296 233 233 296l47 69c-12 21-21 46-29 71L167 458v84l84 25c8 25 17 50 29 75L233 708 292 767l76-44c21 12 46 25 75 29L458 833h84l19-81c25-8 50-17 75-29L708 767l59-59-44-66c12-21 25-46 29-75L833 542v-84z m-333 217c-96 0-175-79-175-175 0-96 79-175 175-175 96 0 175 79 175 175 0 96-79 175-175 175z"></path></g>
<g id="lumo:cross"><path d="M445 500l-142-141c-15-15-15-40 0-56 15-15 40-15 56 0L500 445l141-142c15-15 40-15 56 0 15 15 15 40 0 56L555 500l142 141c15 15 15 40 0 56-15 15-40 15-56 0L500 555l-141 142c-15 15-40 15-56 0-15-15-15-40 0-56L445 500z"></path></g>
<g id="lumo:download"><path d="M538 521l125-112c15-14 39-12 53 4 14 15 12 39-4 53l-187 166a38 38 0 0 1 0 0c-14 13-36 12-50 0l-187-166c-15-14-17-37-4-53 14-15 37-17 53-4L462 521V188c0-21 17-38 38-38s38 17 37 38v333zM758 704c0-21 17-38 38-37 21 0 38 17 37 37v92c0 21-17 38-37 37H204c-21 0-38-17-37-37v-92c0-21 17-38 37-37s38 17 38 37v54h516v-54z"></path></g>
<g id="lumo:dropdown"><path d="M317 393c-15-14-39-13-53 3-14 15-13 39 3 53l206 189c14 13 36 13 50 0l210-193c15-14 17-38 3-53-14-15-38-17-53-3l-185 171L317 393z"></path></g>
<g id="lumo:edit"><path d="M673 281l62 56-205 233c-9 10-38 24-85 39a8 8 0 0 1-5 0c-4-1-7-6-6-10l0 0c14-47 25-76 35-86l204-232z m37-42l52-59c15-17 41-18 58-2 17 16 18 42 3 59L772 295l-62-56zM626 208l-67 75h-226C305 283 283 306 283 333v334C283 695 306 717 333 717h334c28 0 50-22 50-50v-185L792 398v269C792 736 736 792 667 792H333C264 792 208 736 208 667V333C208 264 264 208 333 208h293z"></path></g>
<g id="lumo:error"><path d="M500 833c-184 0-333-149-333-333s149-333 333-333 333 149 333 333-149 333-333 333z m0-68c146 0 265-118 265-265 0-146-118-265-265-265-146 0-265 118-265 265 0 146 118 265 265 265zM479 292h42c12 0 21 9 20 20l-11 217c0 8-6 13-13 13h-34c-7 0-13-6-13-13l-11-217C459 301 468 292 479 292zM483 608h34c12 0 21 9 20 21v33c0 12-9 21-20 21h-34c-12 0-21-9-21-21v-33c0-12 9-21 21-21z"></path></g>
<g id="lumo:eye"><path d="M500 750c-187 0-417-163-417-250s230-250 417-250 417 163 417 250-230 250-417 250z m-336-231c20 22 47 46 78 69C322 644 411 678 500 678s178-34 258-90c31-22 59-46 78-69 6-7 12-14 16-19-4-6-9-12-16-19-20-22-47-46-78-69C678 356 589 322 500 322s-178 34-258 90c-31 22-59 46-78 69-6 7-12 14-16 19 4 6 9 12 16 19zM500 646c-81 0-146-65-146-146s65-146 146-146 146 65 146 146-65 146-146 146z m0-75c39 0 71-32 71-71 0-39-32-71-71-71-39 0-71 32-71 71 0 39 32 71 71 71z"></path></g>
<g id="lumo:eye-disabled"><path d="M396 735l60-60c15 2 30 3 44 3 89 0 178-34 258-90 31-22 59-46 78-69 6-7 12-14 16-19-4-6-9-12-16-19-20-22-47-46-78-69-8-5-15-11-23-15l50-51C862 397 917 458 917 500c0 87-230 250-417 250-34 0-69-5-104-15zM215 654C138 603 83 542 83 500c0-87 230-250 417-250 34 0 69 5 104 15l-59 60c-15-2-30-3-45-3-89 0-178 34-258 90-31 22-59 46-78 69-6 7-12 14-16 19 4 6 9 12 16 19 20 22 47 46 78 69 8 5 16 11 24 16L215 654z m271-9l159-159c0 5 1 9 1 14 0 81-65 146-146 146-5 0-9 0-14-1z m-131-131C354 510 354 505 354 500c0-81 65-146 146-146 5 0 10 0 14 1l-159 159z m-167 257L780 179c12-12 32-12 44 0 12 12 12 32 0 44L232 815c-12 12-32 12-44 0s-12-32 0-44z"></path></g>
<g id="lumo:menu"><path d="M167 292c0-23 19-42 41-42h584C815 250 833 268 833 292c0 23-19 42-41 41H208C185 333 167 315 167 292z m0 208c0-23 19-42 41-42h584C815 458 833 477 833 500c0 23-19 42-41 42H208C185 542 167 523 167 500z m0 208c0-23 19-42 41-41h584C815 667 833 685 833 708c0 23-19 42-41 42H208C185 750 167 732 167 708z"></path></g>
<g id="lumo:minus"><path d="M261 461c-22 0-39 18-39 39 0 22 18 39 39 39h478c22 0 39-18 39-39 0-22-18-39-39-39H261z"></path></g>
<g id="lumo:ordered-list"><path d="M138 333V198H136l-43 28v-38l45-31h45V333H138z m-61 128c0-35 27-59 68-59 39 0 66 21 66 53 0 20-11 37-43 64l-29 27v2h74V583H80v-30l55-52c26-24 32-33 33-43 0-13-10-22-24-22-15 0-26 10-26 25v1h-41v-1zM123 759v-31h21c15 0 25-8 25-21 0-13-10-21-25-21-15 0-26 9-26 23h-41c1-34 27-56 68-57 39 0 66 20 66 49 0 20-14 36-33 39v3c24 3 40 19 39 41 0 32-30 54-73 54-41 0-69-22-70-57h43c1 13 11 22 28 22 16 0 27-9 27-22 0-14-10-22-28-22h-21zM333 258c0-18 15-33 34-33h516c18 0 33 15 34 33 0 18-15 33-34 34H367c-18 0-33-15-34-34z m0 250c0-18 15-33 34-33h516c18 0 33 15 34 33s-15 33-34 34H367c-18 0-33-15-34-34z m0 250c0-18 15-33 34-33h516c18 0 33 15 34 33s-15 33-34 34H367c-18 0-33-15-34-34z"></path></g>
<g id="lumo:phone"><path d="M296 208l42-37c17-15 44-13 58 4a42 42 0 0 1 5 7L459 282c12 20 5 45-15 57l-7 4c-17 10-25 30-19 48l20 66a420 420 0 0 0 93 157l41 45c13 14 35 17 51 8l7-5c20-12 45-5 57 16L745 777c12 20 5 45-15 57a42 42 0 0 1-8 4l-52 17c-61 21-129 4-174-43l-50-52c-81-85-141-189-175-302l-24-78c-19-62 0-129 49-172z"></path></g>
<g id="lumo:photo"><path d="M208 167h584c69 0 125 56 125 125v416c0 69-56 125-125 125H208c-69 0-125-56-125-125V292c0-69 56-125 125-125z m584 75H208c-28 0-50 22-50 50v416c0 28 22 50 50 50h584c28 0 50-22 50-50V292c0-28-22-50-50-50zM239 740l167-167c12-12 31-14 45-6l73 43 172-201c13-15 34-18 50-7l95 67v92l-111-78-169 199c-12 14-32 17-47 8l-76-43-111 111H229c2-7 5-13 10-18zM458 427C458 490 407 542 344 542S229 490 229 427c0-63 51-115 115-115S458 364 458 427z m-62 0C396 398 373 375 344 375S292 398 292 427c0 29 23 52 52 52s52-23 52-52z"></path></g>
<g id="lumo:play"><path d="M689 528l-298 175c-13 8-34 8-48 0-6-4-10-9-10-14V311C333 300 348 292 367 292c9 0 17 2 24 5l298 175c26 15 26 40 0 56z"></path></g>
<g id="lumo:plus"><path d="M461 461H261c-22 0-39 18-39 39 0 22 18 39 39 39h200v200c0 22 18 39 39 39 22 0 39-18 39-39v-200h200c22 0 39-18 39-39 0-22-18-39-39-39h-200V261c0-22-18-39-39-39-22 0-39 18-39 39v200z"></path></g>
<g id="lumo:redo"><path d="M290 614C312 523 393 458 491 458c55 0 106 22 144 57l-88 88c-3 3-5 7-5 11 0 8 6 15 15 15l193-5c17 0 31-15 31-32l5-192c0-4-1-8-4-11-6-6-16-6-22 0l-66 67C641 406 570 375 491 375c-136 0-248 90-281 215-1 2-1 5-1 8-8 44 45 68 73 32 4-5 7-11 8-16z"></path></g>
<g id="lumo:reload"><path d="M500 233V137c0-9 7-16 15-16 4 0 8 2 10 4l133 140c12 12 12 32 0 45l-133 140c-6 6-15 6-21 0C502 447 500 443 500 438V308c-117 0-212 95-212 213 0 117 95 212 212 212 117 0 212-95 212-212 0-21 17-38 38-38s38 17 37 38c0 159-129 288-287 287-159 0-288-129-288-287 0-159 129-288 288-288z"></path></g>
<g id="lumo:search"><path d="M662 603l131 131c16 16 16 42 0 59-16 16-43 16-59 0l-131-131C562 691 512 708 458 708c-138 0-250-112-250-250 0-138 112-250 250-250 138 0 250 112 250 250 0 54-17 104-46 145zM458 646c104 0 188-84 188-188S562 271 458 271 271 355 271 458s84 188 187 188z"></path></g>
<g id="lumo:undo"><path d="M710 614C688 523 607 458 509 458c-55 0-106 22-144 57l88 88c3 3 5 7 5 11 0 8-6 15-15 15l-193-5c-17 0-31-15-31-32L214 400c0-4 1-8 4-11 6-6 16-6 22 0l66 67C359 406 430 375 509 375c136 0 248 90 281 215 1 2 1 5 1 8 8 44-45 68-73 32-4-5-7-11-8-16z"></path></g>
<g id="lumo:unordered-list"><path d="M146 325c-42 0-67-26-67-63 0-37 25-63 67-63 42 0 67 26 67 63 0 37-25 63-67 63z m0 250c-42 0-67-26-67-63 0-37 25-63 67-63 42 0 67 26 67 63 0 37-25 63-67 63z m0 250c-42 0-67-26-67-63 0-37 25-63 67-63 42 0 67 26 67 63 0 37-25 63-67 63zM333 258c0-18 15-33 34-33h516c18 0 33 15 34 33 0 18-15 33-34 34H367c-18 0-33-15-34-34z m0 250c0-18 15-33 34-33h516c18 0 33 15 34 33s-15 33-34 34H367c-18 0-33-15-34-34z m0 250c0-18 15-33 34-33h516c18 0 33 15 34 33s-15 33-34 34H367c-18 0-33-15-34-34z"></path></g>
<g id="lumo:upload"><path d="M454 271V604c0 21-17 38-37 38s-38-17-38-38V271L254 382c-15 14-39 12-53-3-14-15-12-39 3-53L391 160c14-13 36-13 51-1 0 0 0 0 0 1l187 166c15 14 17 37 3 53-14 15-37 17-53 3L454 271zM675 704c0-21 17-38 37-37 21 0 38 17 38 37v92c0 21-17 38-38 37H121c-21 0-38-17-38-37v-92c0-21 17-38 38-37s38 17 37 37v54h517v-54z"></path></g>
<g id="lumo:user"><path d="M500 500c-69 0-125-56-125-125s56-125 125-125 125 56 125 125-56 125-125 125z m-292 292c0-115 131-208 292-209s292 93 292 209H208z"></path></g>
</defs></svg>`;di.register("lumo",1e3,ci);Ei(Ni.toString(),"CSSImport end",document);const Pa=o=>{const e=[];return o==="c5c23775eb19b4efbed424cc6f9969f62a4f5de996260c6d0067844f36cd9d85"&&e.push(E(()=>Promise.resolve().then(()=>Ta),void 0,import.meta.url)),o==="793c4e37c9e24f278c689fdb7cc9f3bde829509233254603a3a91985019412ae"&&e.push(E(()=>import("./chunk-e666b5e1b4a9638eb6680d7e0ef5a6c5ac3f59445f944d66d189c9953f7f27e0-CzS2HOv0.js"),__vite__mapDeps([0,1]),import.meta.url)),o==="462d21c63a53a19426c53f98c3e9801d9efef639e9445a294ab61d10e124a984"&&e.push(E(()=>import("./chunk-ed8ec06b8d35094e9048a92bd79b6727334c4e2cec7a6d66d2eb10970b5cc77f-DCfhFmFo.js"),__vite__mapDeps([2,3,1,0,4,5,6,7]),import.meta.url)),o==="222817c46767482e50efe6dac7e3a8dcd12af9a3aeb346f6dd7680cd94f90cf3"&&e.push(E(()=>import("./chunk-2960d5167274dbf89489bfca94c9d84699ecfac2206f97049dab91d462a95ea2-C22sHrFy.js"),__vite__mapDeps([5,3,1,0,6,7]),import.meta.url)),o==="19c59c8a14c569d781cb64888e7cba85936128785bf738c8a2c36269b0ec9029"&&e.push(E(()=>import("./chunk-c46eff7c90a2615d80f789790ea0702e379ede7f5805fd66528ca0712487618c-zexPm7Ez.js"),__vite__mapDeps([8,3,1,0,4,9,7,6]),import.meta.url)),o==="1d2c11dbe97d4be441bced455194fe5b88c641f0ee1840abd4417f13aaca6772"&&e.push(E(()=>import("./chunk-ed8ec06b8d35094e9048a92bd79b6727334c4e2cec7a6d66d2eb10970b5cc77f-DCfhFmFo.js"),__vite__mapDeps([2,3,1,0,4,5,6,7]),import.meta.url)),o==="c099f693e4beb37ab950e3432d0f5f1ab194ca920c4512bd5ec1dab5c2c4d837"&&e.push(E(()=>import("./chunk-ed8ec06b8d35094e9048a92bd79b6727334c4e2cec7a6d66d2eb10970b5cc77f-DCfhFmFo.js"),__vite__mapDeps([2,3,1,0,4,5,6,7]),import.meta.url)),o==="a6c7153eec8370a718c3f74c1c601f9cfea6df62dfe29261d59549115a381b32"&&e.push(E(()=>import("./chunk-e666b5e1b4a9638eb6680d7e0ef5a6c5ac3f59445f944d66d189c9953f7f27e0-CzS2HOv0.js"),__vite__mapDeps([0,1]),import.meta.url)),o==="3a0fc0e77c5e8b04828e9ee517c2683b427e9e5f503d00dcc087eca89cc8b995"&&e.push(E(()=>import("./chunk-e11278c51e7a4866a3f7e5b4d37c1ba52678e971eaeb26237e3a4acbad002b33-DzQyTbjc.js").then(t=>t.c),__vite__mapDeps([3,1,0]),import.meta.url)),o==="7d27158770071726018eac5fc510c7acb51df8089bbbcd981d6984ec4ee6ad1e"&&e.push(E(()=>import("./chunk-710ececcf1e01181c15630171cb7974ce6e703c3f8cdbc15310e188ab49ca555-DYmhFkRI.js"),__vite__mapDeps([9,3,1,0,4,7]),import.meta.url)),o==="ca01ce2c21a9e77a5125d2aaa4c0e53ca50161a5b2b4319a5a80cff37e60181a"&&e.push(E(()=>import("./chunk-710ececcf1e01181c15630171cb7974ce6e703c3f8cdbc15310e188ab49ca555-DYmhFkRI.js"),__vite__mapDeps([9,3,1,0,4,7]),import.meta.url)),Promise.all(e)};window.Vaadin=window.Vaadin||{};window.Vaadin.Flow=window.Vaadin.Flow||{};window.Vaadin.Flow.loadOnDemand=Pa;window.Vaadin.Flow.resetFocus=()=>{let o=document.activeElement;for(;o&&o.shadowRoot;)o=o.shadowRoot.activeElement;return!o||o.blur()||o.focus()||!0};const bl=Object.freeze(Object.defineProperty({__proto__:null},Symbol.toStringTag,{value:"Module"})),Ta=Object.freeze(Object.defineProperty({__proto__:null},Symbol.toStringTag,{value:"Module"}));export{Ia as $,Ts as A,ai as B,at as C,ue as D,lt as E,$o as F,vs as G,qn as H,Un as I,Gi as J,me as K,Wn as L,ji as M,v as N,Hs as O,F as P,as as Q,I as R,dt as S,nt as T,Yt as U,ti as V,$i as W,Zi as X,D as Y,hr as Z,za as _,fe as a,Y as a0,nn as a1,$a as a2,Ka as a3,Ho as a4,sn as a5,rn as a6,Ba as a7,Ke as a8,ye as a9,At as aa,ua as ab,Va as ac,dn as ad,Vs as ae,Ds as af,Us as ag,ct as ah,_l as ai,No as aj,Jo as ak,Fa as al,Ha as am,Ra as an,Vn as ao,Hn as ap,Sn as aq,Na as ar,Wo as as,bl as at,Do as b,ms as c,P as d,ln as e,io as f,Bo as g,z as h,Cs as i,ws as j,St as k,qs as l,Zo as m,Ko as n,lo as o,zs as p,Pn as q,g as r,jn as s,Tn as t,Go as u,Ma as v,ei as w,js as x,Ls as y,Da as z};
function __vite__mapDeps(indexes) {
  if (!__vite__mapDeps.viteFileDeps) {
    __vite__mapDeps.viteFileDeps = ["./chunk-e666b5e1b4a9638eb6680d7e0ef5a6c5ac3f59445f944d66d189c9953f7f27e0-CzS2HOv0.js","./indexhtml-CHKSUbna.js","./chunk-ed8ec06b8d35094e9048a92bd79b6727334c4e2cec7a6d66d2eb10970b5cc77f-DCfhFmFo.js","./chunk-e11278c51e7a4866a3f7e5b4d37c1ba52678e971eaeb26237e3a4acbad002b33-DzQyTbjc.js","./comboBoxConnector-CEbbwbO1.js","./chunk-2960d5167274dbf89489bfca94c9d84699ecfac2206f97049dab91d462a95ea2-C22sHrFy.js","./vaadin-form-item-a0wv67LN.js","./vaadin-notification-BiteM-H-.js","./chunk-c46eff7c90a2615d80f789790ea0702e379ede7f5805fd66528ca0712487618c-zexPm7Ez.js","./chunk-710ececcf1e01181c15630171cb7974ce6e703c3f8cdbc15310e188ab49ca555-DYmhFkRI.js"]
  }
  return indexes.map((i) => __vite__mapDeps.viteFileDeps[i])
}