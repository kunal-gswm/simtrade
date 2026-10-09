var qt=Object.defineProperty;var jt=(r,i,e)=>i in r?qt(r,i,{enumerable:!0,configurable:!0,writable:!0,value:e}):r[i]=e;var z=(r,i,e)=>(jt(r,typeof i!="symbol"?i+"":i,e),e);import{H as Kt,J as Yt,M as Ie,N as _t,Q as P,R as ft,S as Qt,U as Xt,W as pt,P as L,X as Zt,Y as Jt,h as Q,Z as ei,r as k,o as ti,_ as x,$ as ii,a0 as j,a1 as he,a2 as K,a3 as si,a4 as B,a5 as mt,a as gt,b as bt,D as pe,d as V,a6 as me,a7 as ri,a8 as Qe,j as ke,a9 as vt,F as ni,T as ae,K as oi,p as ai,aa as re,ab as li,ac as di,C as $e,ad as hi,l as ci,ae as Ct,c as ui,O as _i,i as Ee,af as Ce,ag as yt,e as fi,E as He,f as pi,ah as mi,ai as gi,aj as Xe,ak as bi,u as xt,al as vi,am as Ci,n as yi,m as xi,an as wi,ao as Ai,t as Ii,L as Ei,ap as Ze,v as Je}from"./chunk-8463e6da51a94694d59b00e3455214aa0d3a0a2d1374f94614beac546593180b-lI97lcEU.js";import{a as Si,t as wt,e as Ti,w as zi,j as At,x as It,i as F}from"./indexhtml-zC7nuYFN.js";import"./chunk-e666b5e1b4a9638eb6680d7e0ef5a6c5ac3f59445f944d66d189c9953f7f27e0-DU8OIKXw.js";/**
 * @license
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const ar=(r,i)=>(r==null?void 0:r._$litType$)!==void 0,Pi=r=>r.strings===void 0;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/class N{constructor(){this._asyncModule=null,this._callback=null,this._timer=null}setConfig(i,e){this._asyncModule=i,this._callback=e,this._timer=this._asyncModule.run(()=>{this._timer=null,fe.delete(this),this._callback()})}cancel(){this.isActive()&&(this._cancelAsync(),fe.delete(this))}_cancelAsync(){this.isActive()&&(this._asyncModule.cancel(this._timer),this._timer=null)}flush(){this.isActive()&&(this.cancel(),this._callback())}isActive(){return this._timer!=null}static debounce(i,e,t){return i instanceof N?i._cancelAsync():i=new N,i.setConfig(e,t),i}}let fe=new Set;const Ri=function(r){fe.add(r)},Oi=function(){const r=!!fe.size;return fe.forEach(i=>{try{i.flush()}catch(e){setTimeout(()=>{throw e})}}),r};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/const ki=function(){let r,i;do r=window.ShadyDOM&&ShadyDOM.flush(),window.ShadyCSS&&window.ShadyCSS.ScopingShim&&window.ShadyCSS.ScopingShim.flush(),i=Oi();while(r||i)};/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let et=!1;function Fi(){if(Kt&&!Yt){if(!et){et=!0;const r=document.createElement("style");r.textContent="dom-bind,dom-if,dom-repeat{display:none;}",document.head.appendChild(r)}return!0}return!1}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/function Ne(r,i,e,t,s){let n;s&&(n=typeof e=="object"&&e!==null,n&&(t=r.__dataTemp[i]));let o=t!==e&&(t===t||e===e);return n&&o&&(r.__dataTemp[i]=e),o}const Be=Ie(r=>{class i extends r{_shouldPropertyChange(t,s,n){return Ne(this,t,s,n,!0)}}return i});Ie(r=>{class i extends r{static get properties(){return{mutableData:Boolean}}_shouldPropertyChange(t,s,n){return Ne(this,t,s,n,this.mutableData)}}return i});Be._mutablePropertyChange=Ne;/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/let Fe=null;function De(){return Fe}De.prototype=Object.create(HTMLTemplateElement.prototype,{constructor:{value:De,writable:!0}});const Et=_t(De),Di=Be(Et);function Mi(r,i){Fe=r,Object.setPrototypeOf(r,i.prototype),new i,Fe=null}const Li=_t(class{});function St(r,i){for(let e=0;e<i.length;e++){let t=i[e];if(!!r!=!!t.__hideTemplateChildren__)if(t.nodeType===Node.TEXT_NODE)r?(t.__polymerTextContent__=t.textContent,t.textContent=""):t.textContent=t.__polymerTextContent__;else if(t.localName==="slot")if(r)t.__polymerReplaced__=document.createComment("hidden-slot"),P(P(t).parentNode).replaceChild(t.__polymerReplaced__,t);else{const s=t.__polymerReplaced__;s&&P(P(s).parentNode).replaceChild(t,s)}else t.style&&(r?(t.__polymerDisplay__=t.style.display,t.style.display="none"):t.style.display=t.__polymerDisplay__);t.__hideTemplateChildren__=r,t._showHideChildren&&t._showHideChildren(r)}}class X extends Li{constructor(i){super(),this._configureProperties(i),this.root=this._stampTemplate(this.__dataHost);let e=[];this.children=e;for(let s=this.root.firstChild;s;s=s.nextSibling)e.push(s),s.__templatizeInstance=this;this.__templatizeOwner&&this.__templatizeOwner.__hideTemplateChildren__&&this._showHideChildren(!0);let t=this.__templatizeOptions;(i&&t.instanceProps||!t.instanceProps)&&this._enableProperties()}_configureProperties(i){if(this.__templatizeOptions.forwardHostProp)for(let t in this.__hostProps)this._setPendingProperty(t,this.__dataHost["_host_"+t]);for(let t in i)this._setPendingProperty(t,i[t])}forwardHostProp(i,e){this._setPendingPropertyOrPath(i,e,!1,!0)&&this.__dataHost._enqueueClient(this)}_addEventListenerToNode(i,e,t){if(this._methodHost&&this.__templatizeOptions.parentModel)this._methodHost._addEventListenerToNode(i,e,s=>{s.model=this,t(s)});else{let s=this.__dataHost.__dataHost;s&&s._addEventListenerToNode(i,e,t)}}_showHideChildren(i){St(i,this.children)}_setUnmanagedPropertyToNode(i,e,t){i.__hideTemplateChildren__&&i.nodeType==Node.TEXT_NODE&&e=="textContent"?i.__polymerTextContent__=t:super._setUnmanagedPropertyToNode(i,e,t)}get parentModel(){let i=this.__parentModel;if(!i){let e;i=this;do i=i.__dataHost.__dataHost;while((e=i.__templatizeOptions)&&!e.parentModel);this.__parentModel=i}return i}dispatchEvent(i){return!0}}X.prototype.__dataHost;X.prototype.__templatizeOptions;X.prototype._methodHost;X.prototype.__templatizeOwner;X.prototype.__hostProps;const $i=Be(X);function tt(r){let i=r.__dataHost;return i&&i._methodHost||i}function Hi(r,i,e){let t=e.mutableData?$i:X;Me.mixin&&(t=Me.mixin(t));let s=class extends t{};return s.prototype.__templatizeOptions=e,s.prototype._bindTemplate(r),Vi(s,r,i,e),s}function Ni(r,i,e,t){let s=e.forwardHostProp;if(s&&i.hasHostProps){const n=r.localName=="template";let o=i.templatizeTemplateClass;if(!o){if(n){let d=e.mutableData?Di:Et;class _ extends d{}o=i.templatizeTemplateClass=_}else{const d=r.constructor;class _ extends d{}o=i.templatizeTemplateClass=_}let a=i.hostProps;for(let d in a)o.prototype._addPropertyEffect("_host_"+d,o.prototype.PROPERTY_EFFECT_TYPES.PROPAGATE,{fn:Bi(d,s)}),o.prototype._createNotifyingProperty("_host_"+d);Qt&&t&&Ui(i,e,t)}if(r.__dataProto&&Object.assign(r.__data,r.__dataProto),n)Mi(r,o),r.__dataTemp={},r.__dataPending=null,r.__dataOld=null,r._enableProperties();else{Object.setPrototypeOf(r,o.prototype);const a=i.hostProps;for(let d in a)if(d="_host_"+d,d in r){const _=r[d];delete r[d],r.__data[d]=_}}}}function Bi(r,i){return function(t,s,n){i.call(t.__templatizeOwner,s.substring(6),n[s])}}function Vi(r,i,e,t){let s=e.hostProps||{};for(let n in t.instanceProps){delete s[n];let o=t.notifyInstanceProp;o&&r.prototype._addPropertyEffect(n,r.prototype.PROPERTY_EFFECT_TYPES.NOTIFY,{fn:Gi(n,o)})}if(t.forwardHostProp&&i.__dataHost)for(let n in s)e.hasHostProps||(e.hasHostProps=!0),r.prototype._addPropertyEffect(n,r.prototype.PROPERTY_EFFECT_TYPES.NOTIFY,{fn:Wi()})}function Gi(r,i){return function(t,s,n){i.call(t.__templatizeOwner,t,s,n[s])}}function Wi(){return function(i,e,t){i.__dataHost._setPendingPropertyOrPath("_host_"+e,t[e],!0,!0)}}function Me(r,i,e){if(ft&&!tt(r))throw new Error("strictTemplatePolicy: template owner not trusted");if(e=e||{},r.__templatizeOwner)throw new Error("A <template> can only be templatized once");r.__templatizeOwner=i;let s=(i?i.constructor:X)._parseTemplate(r),n=s.templatizeInstanceClass;n||(n=Hi(r,s,e),s.templatizeInstanceClass=n);const o=tt(r);Ni(r,s,e,o);let a=class extends n{};return a.prototype._methodHost=o,a.prototype.__dataHost=r,a.prototype.__templatizeOwner=i,a.prototype.__hostProps=s.hostProps,a=a,a}function Ui(r,i,e){const t=e.constructor._properties,{propertyEffects:s}=r,{instanceProps:n}=i;for(let o in s)if(!t[o]&&!(n&&n[o])){const a=s[o];for(let d=0;d<a.length;d++){const{part:_}=a[d].info;if(!(_.signature&&_.signature.static)){console.warn(`Property '${o}' used in template but not declared in 'properties'; attribute will not be observed.`);break}}}}/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/class Tt extends L{static get is(){return"dom-if"}static get template(){return null}static get properties(){return{if:{type:Boolean,observer:"__debounceRender"},restamp:{type:Boolean,observer:"__debounceRender"},notifyDomChange:{type:Boolean}}}constructor(){super(),this.__renderDebouncer=null,this._lastIf=!1,this.__hideTemplateChildren__=!1,this.__template,this._templateInfo}__debounceRender(){this.__renderDebouncer=N.debounce(this.__renderDebouncer,Zt,()=>this.__render()),Ri(this.__renderDebouncer)}disconnectedCallback(){super.disconnectedCallback();const i=P(this).parentNode;(!i||i.nodeType==Node.DOCUMENT_FRAGMENT_NODE&&!P(i).host)&&this.__teardownInstance()}connectedCallback(){super.connectedCallback(),Fi()||(this.style.display="none"),this.if&&this.__debounceRender()}__ensureTemplate(){if(!this.__template){const i=this;let e=i._templateInfo?i:P(i).querySelector("template");if(!e){let t=new MutationObserver(()=>{if(P(this).querySelector("template"))t.disconnect(),this.__render();else throw new Error("dom-if requires a <template> child")});return t.observe(this,{childList:!0}),!1}this.__template=e}return!0}__ensureInstance(){let i=P(this).parentNode;if(this.__hasInstance()){let e=this.__getInstanceNodes();if(e&&e.length&&P(this).previousSibling!==e[e.length-1])for(let s=0,n;s<e.length&&(n=e[s]);s++)P(i).insertBefore(n,this)}else{if(!i||!this.__ensureTemplate())return!1;this.__createAndInsertInstance(i)}return!0}render(){ki()}__render(){if(this.if){if(!this.__ensureInstance())return}else this.restamp&&this.__teardownInstance();this._showHideChildren(),(!Jt||this.notifyDomChange)&&this.if!=this._lastIf&&(this.dispatchEvent(new CustomEvent("dom-change",{bubbles:!0,composed:!0})),this._lastIf=this.if)}__hasInstance(){}__getInstanceNodes(){}__createAndInsertInstance(i){}__teardownInstance(){}_showHideChildren(){}}class qi extends Tt{constructor(){super(),this.__instance=null,this.__syncInfo=null}__hasInstance(){return!!this.__instance}__getInstanceNodes(){return this.__instance.templateInfo.childNodes}__createAndInsertInstance(i){const e=this.__dataHost||this;if(ft&&!this.__dataHost)throw new Error("strictTemplatePolicy: template owner not trusted");const t=e._bindTemplate(this.__template,!0);t.runEffects=(s,n,o)=>{let a=this.__syncInfo;if(this.if)a&&(this.__syncInfo=null,this._showHideChildren(),n=Object.assign(a.changedProps,n)),s(n,o);else if(this.__instance)if(a||(a=this.__syncInfo={runEffects:s,changedProps:{}}),o)for(const d in n){const _=pt(d);a.changedProps[_]=this.__dataHost[_]}else Object.assign(a.changedProps,n)},this.__instance=e._stampTemplate(this.__template,t),P(i).insertBefore(this.__instance,this)}__syncHostProperties(){const i=this.__syncInfo;i&&(this.__syncInfo=null,i.runEffects(i.changedProps,!1))}__teardownInstance(){const i=this.__dataHost||this;this.__instance&&(i._removeBoundDom(this.__instance),this.__instance=null,this.__syncInfo=null)}_showHideChildren(){const i=this.__hideTemplateChildren__||!this.if;this.__instance&&!!this.__instance.__hidden!==i&&(this.__instance.__hidden=i,St(i,this.__instance.templateInfo.childNodes)),i||this.__syncHostProperties()}}class ji extends Tt{constructor(){super(),this.__ctor=null,this.__instance=null,this.__invalidProps=null}__hasInstance(){return!!this.__instance}__getInstanceNodes(){return this.__instance.children}__createAndInsertInstance(i){this.__ctor||(this.__ctor=Me(this.__template,this,{mutableData:!0,forwardHostProp:function(e,t){this.__instance&&(this.if?this.__instance.forwardHostProp(e,t):(this.__invalidProps=this.__invalidProps||Object.create(null),this.__invalidProps[pt(e)]=!0))}})),this.__instance=new this.__ctor,P(i).insertBefore(this.__instance.root,this)}__teardownInstance(){if(this.__instance){let i=this.__instance.children;if(i&&i.length){let e=P(i[0]).parentNode;if(e){e=P(e);for(let t=0,s;t<i.length&&(s=i[t]);t++)e.removeChild(s)}}this.__invalidProps=null,this.__instance=null}}__syncHostProperties(){let i=this.__invalidProps;if(i){this.__invalidProps=null;for(let e in i)this.__instance._setPendingProperty(e,this.__dataHost[e]);this.__instance._flushProperties()}}_showHideChildren(){const i=this.__hideTemplateChildren__||!this.if;this.__instance&&!!this.__instance.__hidden!==i&&(this.__instance.__hidden=i,this.__instance._showHideChildren(i)),i||this.__syncHostProperties()}}const it=Xt?qi:ji;customElements.define(it.is,it);/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const ue=(r,i)=>{var t;const e=r._$AN;if(e===void 0)return!1;for(const s of e)(t=s._$AO)==null||t.call(s,i,!1),ue(s,i);return!0},ye=r=>{let i,e;do{if((i=r._$AM)===void 0)break;e=i._$AN,e.delete(r),r=i}while((e==null?void 0:e.size)===0)},zt=r=>{for(let i;i=r._$AM;r=i){let e=i._$AN;if(e===void 0)i._$AN=e=new Set;else if(e.has(r))break;e.add(r),Qi(i)}};function Ki(r){this._$AN!==void 0?(ye(this),this._$AM=r,zt(this)):this._$AM=r}function Yi(r,i=!1,e=0){const t=this._$AH,s=this._$AN;if(s!==void 0&&s.size!==0)if(i)if(Array.isArray(t))for(let n=e;n<t.length;n++)ue(t[n],!1),ye(t[n]);else t!=null&&(ue(t,!1),ye(t));else ue(this,r)}const Qi=r=>{r.type==wt.CHILD&&(r._$AP??(r._$AP=Yi),r._$AQ??(r._$AQ=Ki))};class Xi extends Si{constructor(){super(...arguments),this._$AN=void 0}_$AT(i,e,t){super._$AT(i,e,t),zt(this),this.isConnected=i._$AU}_$AO(i,e=!0){var t,s;i!==this.isConnected&&(this.isConnected=i,i?(t=this.reconnected)==null||t.call(this):(s=this.disconnected)==null||s.call(this)),e&&(ue(this,i),ye(this))}setValue(i){if(Pi(this._$Ct))this._$Ct._$AI(i,this);else{const e=[...this._$Ct._$AH];e[this._$Ci]=i,this._$Ct._$AI(e,this,0)}}disconnected(){}reconnected(){}}class Zi extends Xi{constructor(i){if(super(i),i.type!==wt.CHILD)throw new Error(`${this.constructor.directiveName}() can only be used in child bindings`)}update(i,[e,t]){return this.updateContent(i,e,t),zi}updateContent(i,e,t){const{parentNode:s,startNode:n}=i,o=t!=null,a=o?this.getNewNode(e,t):null,d=this.getOldNode(i);if(clearTimeout(this.__nodeRetryTimeout),o&&!a)this.__nodeRetryTimeout=setTimeout(()=>this.updateContent(i,e,t));else{if(d===a)return;d&&a?s.replaceChild(a,d):d?s.removeChild(d):a&&n.after(a)}}getNewNode(i,e){return window.Vaadin.Flow.clients[i].getByNodeId(e)}getOldNode(i){const{startNode:e,endNode:t}=i;if(e.nextSibling!==t)return e.nextSibling}disconnected(){clearTimeout(this.__nodeRetryTimeout)}}const Pt=Ti(Zi);function Ji(r,i){return Pt(r,i)}function es(r,i,e){At(It`${i.map(t=>Pt(r,t))}`,e)}function ts(r){const i=r.insertBefore;r.insertBefore=function(e,t){return t&&t.parentNode===this?i.call(this,e,t):i.call(this,e,null)}}window.Vaadin||(window.Vaadin={});var ut;(ut=window.Vaadin).FlowComponentHost||(ut.FlowComponentHost={patchVirtualContainer:ts,getNode:Ji,setChildNodes:es});class st extends L{static get template(){return Q`
      <style>
        :host {
          animation: 1ms flow-component-renderer-appear;
        }

        @keyframes flow-component-renderer-appear {
          to {
            opacity: 1;
          }
        }
      </style>
      <slot></slot>
    `}static get is(){return"flow-component-renderer"}static get properties(){return{nodeid:Number,appid:String}}static get observers(){return["_attachRenderedComponentIfAble(appid, nodeid)"]}ready(){super.ready(),this.addEventListener("click",function(i){this.firstChild&&typeof this.firstChild.click=="function"&&i.target===this&&(i.stopPropagation(),this.firstChild.click())}),this.addEventListener("animationend",this._onAnimationEnd)}_asyncAttachRenderedComponentIfAble(){this._debouncer=N.debounce(this._debouncer,ei,()=>this._attachRenderedComponentIfAble())}_attachRenderedComponentIfAble(){if(!this.nodeid||!this.appid)return;const i=this._getRenderedComponent();this.firstChild?i?this.firstChild!==i?(this.replaceChild(i,this.firstChild),this._defineFocusTarget(),this.onComponentRendered()):(this._defineFocusTarget(),this.onComponentRendered()):this._asyncAttachRenderedComponentIfAble():i?(this.appendChild(i),this._defineFocusTarget(),this.onComponentRendered()):this._asyncAttachRenderedComponentIfAble()}_getRenderedComponent(){try{return window.Vaadin.Flow.clients[this.appid].getByNodeId(this.nodeid)}catch(i){console.error("Could not get node %s from app %s",this.nodeid,this.appid),console.error(i)}return null}onComponentRendered(){}_defineFocusTarget(){var i=this._getFirstFocusableDescendant(this.firstChild);i!==null&&i.setAttribute("focus-target","true")}_getFirstFocusableDescendant(i){if(this._isFocusable(i))return i;if(i.hasAttribute&&(i.hasAttribute("disabled")||i.hasAttribute("hidden"))||!i.children)return null;for(var e=0;e<i.children.length;e++){var t=this._getFirstFocusableDescendant(i.children[e]);if(t!==null)return t}return null}_isFocusable(i){return i.hasAttribute&&typeof i.hasAttribute=="function"&&(i.hasAttribute("disabled")||i.hasAttribute("hidden"))?!1:i.tabIndex===0}_onAnimationEnd(i){i.animationName.indexOf("flow-component-renderer-appear")===0&&this._attachRenderedComponentIfAble()}}window.customElements.define(st.is,st);const Rt=F`
  :host {
    display: flex;
    align-items: center;
    box-sizing: border-box;
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-m);
    line-height: var(--lumo-line-height-xs);
    padding: 0.5em calc(var(--lumo-space-l) + var(--lumo-border-radius-m) / 4) 0.5em
      var(--_lumo-list-box-item-padding-left, calc(var(--lumo-border-radius-m) / 4));
    min-height: var(--lumo-size-m);
    outline: none;
    border-radius: var(--lumo-border-radius-m);
    cursor: var(--lumo-clickable-cursor);
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    -webkit-tap-highlight-color: var(--lumo-primary-color-10pct);
    --_focus-ring-color: var(--vaadin-focus-ring-color, var(--lumo-primary-color-50pct));
    --_focus-ring-width: var(--vaadin-focus-ring-width, 2px);
    --_selection-color-text: var(--vaadin-selection-color-text, var(--lumo-primary-text-color));
  }

  /* Checkmark */
  [part='checkmark']::before {
    display: var(--_lumo-item-selected-icon-display, none);
    content: var(--lumo-icons-checkmark);
    font-family: lumo-icons;
    font-size: var(--lumo-icon-size-m);
    line-height: 1;
    font-weight: normal;
    width: 1em;
    height: 1em;
    margin: calc((1 - var(--lumo-line-height-xs)) * var(--lumo-font-size-m) / 2) 0;
    color: var(--_selection-color-text);
    flex: none;
    opacity: 0;
    transition: transform 0.2s cubic-bezier(0.12, 0.32, 0.54, 2), opacity 0.1s;
  }

  :host([selected]) [part='checkmark']::before {
    opacity: 1;
  }

  :host([active]:not([selected])) [part='checkmark']::before {
    transform: scale(0.8);
    opacity: 0;
    transition-duration: 0s;
  }

  [part='content'] {
    flex: auto;
  }

  /* Disabled */
  :host([disabled]) {
    color: var(--lumo-disabled-text-color);
    cursor: default;
    pointer-events: none;
  }

  /* TODO a workaround until we have "focus-follows-mouse". After that, use the hover style for focus-ring as well */
  @media (any-hover: hover) {
    :host(:hover:not([disabled])) {
      background-color: var(--lumo-primary-color-10pct);
    }

    :host([focus-ring]:not([disabled])) {
      box-shadow: inset 0 0 0 var(--_focus-ring-width) var(--_focus-ring-color);
    }
  }

  /* RTL specific styles */
  :host([dir='rtl']) {
    padding-left: calc(var(--lumo-space-l) + var(--lumo-border-radius-m) / 4);
    padding-right: var(--_lumo-list-box-item-padding-left, calc(var(--lumo-border-radius-m) / 4));
  }

  /* Slotted icons */
  :host ::slotted(vaadin-icon) {
    width: var(--lumo-icon-size-m);
    height: var(--lumo-icon-size-m);
  }
`;k("vaadin-item",Rt,{moduleId:"lumo-item"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ot=F`
  :host([opening]),
  :host([closing]) {
    animation: 0.14s lumo-overlay-dummy-animation;
  }

  [part='overlay'] {
    will-change: opacity, transform;
  }

  :host([opening]) [part='overlay'] {
    animation: 0.1s lumo-menu-overlay-enter ease-out both;
  }

  @keyframes lumo-menu-overlay-enter {
    0% {
      opacity: 0;
      transform: translateY(-4px);
    }
  }

  :host([closing]) [part='overlay'] {
    animation: 0.1s lumo-menu-overlay-exit both;
  }

  @keyframes lumo-menu-overlay-exit {
    100% {
      opacity: 0;
    }
  }
`;k("",Ot,{moduleId:"lumo-menu-overlay-core"});const is=F`
  /* Small viewport (bottom sheet) styles */
  /* Use direct media queries instead of the state attributes ([phone] and [fullscreen]) provided by the elements */
  @media (max-width: 420px), (max-height: 420px) {
    :host {
      top: 0 !important;
      right: 0 !important;
      bottom: var(--vaadin-overlay-viewport-bottom, 0) !important;
      left: 0 !important;
      align-items: stretch !important;
      justify-content: flex-end !important;
    }

    [part='overlay'] {
      max-height: 50vh;
      width: 100vw;
      border-radius: 0;
      box-shadow: var(--lumo-box-shadow-xl);
    }

    /* The content part scrolls instead of the overlay part, because of the gradient fade-out */
    [part='content'] {
      padding: 30px var(--lumo-space-m);
      max-height: inherit;
      box-sizing: border-box;
      -webkit-overflow-scrolling: touch;
      overflow: auto;
      -webkit-mask-image: linear-gradient(transparent, #000 40px, #000 calc(100% - 40px), transparent);
      mask-image: linear-gradient(transparent, #000 40px, #000 calc(100% - 40px), transparent);
    }

    [part='backdrop'] {
      display: block;
    }

    /* Animations */

    :host([opening]) [part='overlay'] {
      animation: 0.2s lumo-mobile-menu-overlay-enter cubic-bezier(0.215, 0.61, 0.355, 1) both;
    }

    :host([closing]),
    :host([closing]) [part='backdrop'] {
      animation-delay: 0.14s;
    }

    :host([closing]) [part='overlay'] {
      animation: 0.14s 0.14s lumo-mobile-menu-overlay-exit cubic-bezier(0.55, 0.055, 0.675, 0.19) both;
    }
  }

  @keyframes lumo-mobile-menu-overlay-enter {
    0% {
      transform: translateY(150%);
    }
  }

  @keyframes lumo-mobile-menu-overlay-exit {
    100% {
      transform: translateY(150%);
    }
  }
`,kt=[ti,Ot,is];k("",kt,{moduleId:"lumo-menu-overlay"});/**
 * @license
 * Copyright (c) 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Ve(r,i){return r.split(".").reduce((e,t)=>e?e[t]:void 0,i)}/**
 * @license
 * Copyright (c) 2016 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
 */const rt=navigator.userAgent.match(/iP(?:hone|ad;(?: U;)? CPU) OS (\d+)/u),ss=rt&&rt[1]>=8,nt=3,rs={_ratio:.5,_scrollerPaddingTop:0,_scrollPosition:0,_physicalSize:0,_physicalAverage:0,_physicalAverageCount:0,_physicalTop:0,_virtualCount:0,_estScrollHeight:0,_scrollHeight:0,_viewportHeight:0,_viewportWidth:0,_physicalItems:null,_physicalSizes:null,_firstVisibleIndexVal:null,_lastVisibleIndexVal:null,_maxPages:2,_templateCost:0,get _physicalBottom(){return this._physicalTop+this._physicalSize},get _scrollBottom(){return this._scrollPosition+this._viewportHeight},get _virtualEnd(){return this._virtualStart+this._physicalCount-1},get _hiddenContentSize(){return this._physicalSize-this._viewportHeight},get _maxScrollTop(){return this._estScrollHeight-this._viewportHeight+this._scrollOffset},get _maxVirtualStart(){const r=this._virtualCount;return Math.max(0,r-this._physicalCount)},get _virtualStart(){return this._virtualStartVal||0},set _virtualStart(r){r=this._clamp(r,0,this._maxVirtualStart),this._virtualStartVal=r},get _physicalStart(){return this._physicalStartVal||0},set _physicalStart(r){r%=this._physicalCount,r<0&&(r=this._physicalCount+r),this._physicalStartVal=r},get _physicalEnd(){return(this._physicalStart+this._physicalCount-1)%this._physicalCount},get _physicalCount(){return this._physicalCountVal||0},set _physicalCount(r){this._physicalCountVal=r},get _optPhysicalSize(){return this._viewportHeight===0?1/0:this._viewportHeight*this._maxPages},get _isVisible(){return!!(this.offsetWidth||this.offsetHeight)},get firstVisibleIndex(){let r=this._firstVisibleIndexVal;if(r==null){let i=this._physicalTop+this._scrollOffset;r=this._iterateItems((e,t)=>{if(i+=this._getPhysicalSizeIncrement(e),i>this._scrollPosition)return t})||0,this._firstVisibleIndexVal=r}return r},get lastVisibleIndex(){let r=this._lastVisibleIndexVal;if(r==null){let i=this._physicalTop+this._scrollOffset;this._iterateItems((e,t)=>{i<this._scrollBottom&&(r=t),i+=this._getPhysicalSizeIncrement(e)}),this._lastVisibleIndexVal=r}return r},get _scrollOffset(){return this._scrollerPaddingTop+this.scrollOffset},_scrollHandler(){const r=Math.max(0,Math.min(this._maxScrollTop,this._scrollTop));let i=r-this._scrollPosition;const e=i>=0;if(this._scrollPosition=r,this._firstVisibleIndexVal=null,this._lastVisibleIndexVal=null,Math.abs(i)>this._physicalSize&&this._physicalSize>0){i-=this._scrollOffset;const t=Math.round(i/this._physicalAverage);this._virtualStart+=t,this._physicalStart+=t,this._physicalTop=Math.min(Math.floor(this._virtualStart)*this._physicalAverage,this._scrollPosition),this._update()}else if(this._physicalCount>0){const t=this._getReusables(e);e?(this._physicalTop=t.physicalTop,this._virtualStart+=t.indexes.length,this._physicalStart+=t.indexes.length):(this._virtualStart-=t.indexes.length,this._physicalStart-=t.indexes.length),this._update(t.indexes,e?null:t.indexes),this._debounce("_increasePoolIfNeeded",this._increasePoolIfNeeded.bind(this,0),K)}},_getReusables(r){let i,e,t;const s=[],n=this._hiddenContentSize*this._ratio,o=this._virtualStart,a=this._virtualEnd,d=this._physicalCount;let _=this._physicalTop+this._scrollOffset;const f=this._physicalBottom+this._scrollOffset,p=this._scrollPosition,g=this._scrollBottom;for(r?(i=this._physicalStart,e=p-_):(i=this._physicalEnd,e=f-g);t=this._getPhysicalSizeIncrement(i),e-=t,!(s.length>=d||e<=n);)if(r){if(a+s.length+1>=this._virtualCount||_+t>=p-this._scrollOffset)break;s.push(i),_+=t,i=(i+1)%d}else{if(o-s.length<=0||_+this._physicalSize-t<=g)break;s.push(i),_-=t,i=i===0?d-1:i-1}return{indexes:s,physicalTop:_-this._scrollOffset}},_update(r,i){if(!(r&&r.length===0||this._physicalCount===0)){if(this._assignModels(r),this._updateMetrics(r),i)for(;i.length;){const e=i.pop();this._physicalTop-=this._getPhysicalSizeIncrement(e)}this._positionItems(),this._updateScrollerSize()}},_isClientFull(){return this._scrollBottom!==0&&this._physicalBottom-1>=this._scrollBottom&&this._physicalTop<=this._scrollPosition},_increasePoolIfNeeded(r){const e=this._clamp(this._physicalCount+r,nt,this._virtualCount-this._virtualStart)-this._physicalCount;let t=Math.round(this._physicalCount*.5);if(!(e<0)){if(e>0){const s=window.performance.now();[].push.apply(this._physicalItems,this._createPool(e));for(let n=0;n<e;n++)this._physicalSizes.push(0);this._physicalCount+=e,this._physicalStart>this._physicalEnd&&this._isIndexRendered(this._focusedVirtualIndex)&&this._getPhysicalIndex(this._focusedVirtualIndex)<this._physicalEnd&&(this._physicalStart+=e),this._update(),this._templateCost=(window.performance.now()-s)/e,t=Math.round(this._physicalCount*.5)}this._virtualEnd>=this._virtualCount-1||t===0||(this._isClientFull()?this._physicalSize<this._optPhysicalSize&&this._debounce("_increasePoolIfNeeded",this._increasePoolIfNeeded.bind(this,this._clamp(Math.round(50/this._templateCost),1,t)),si):this._debounce("_increasePoolIfNeeded",this._increasePoolIfNeeded.bind(this,t),K))}},_render(){if(!(!this.isAttached||!this._isVisible))if(this._physicalCount!==0){const r=this._getReusables(!0);this._physicalTop=r.physicalTop,this._virtualStart+=r.indexes.length,this._physicalStart+=r.indexes.length,this._update(r.indexes),this._update(),this._increasePoolIfNeeded(0)}else this._virtualCount>0&&(this.updateViewportBoundaries(),this._increasePoolIfNeeded(nt))},_itemsChanged(r){r.path==="items"&&(this._virtualStart=0,this._physicalTop=0,this._virtualCount=this.items?this.items.length:0,this._physicalIndexForKey={},this._firstVisibleIndexVal=null,this._lastVisibleIndexVal=null,this._physicalItems||(this._physicalItems=[]),this._physicalSizes||(this._physicalSizes=[]),this._physicalStart=0,this._scrollTop>this._scrollOffset&&this._resetScrollPosition(0),this._debounce("_render",this._render,j))},_iterateItems(r,i){let e,t,s,n;if(arguments.length===2&&i){for(n=0;n<i.length;n++)if(e=i[n],t=this._computeVidx(e),(s=r.call(this,e,t))!=null)return s}else{for(e=this._physicalStart,t=this._virtualStart;e<this._physicalCount;e++,t++)if((s=r.call(this,e,t))!=null)return s;for(e=0;e<this._physicalStart;e++,t++)if((s=r.call(this,e,t))!=null)return s}},_computeVidx(r){return r>=this._physicalStart?this._virtualStart+(r-this._physicalStart):this._virtualStart+(this._physicalCount-this._physicalStart)+r},_positionItems(){this._adjustScrollPosition();let r=this._physicalTop;this._iterateItems(i=>{this.translate3d(0,`${r}px`,0,this._physicalItems[i]),r+=this._physicalSizes[i]})},_getPhysicalSizeIncrement(r){return this._physicalSizes[r]},_adjustScrollPosition(){const r=this._virtualStart===0?this._physicalTop:Math.min(this._scrollPosition+this._physicalTop,0);if(r!==0){this._physicalTop-=r;const i=this._scrollPosition;!ss&&i>0&&this._resetScrollPosition(i-r)}},_resetScrollPosition(r){this.scrollTarget&&r>=0&&(this._scrollTop=r,this._scrollPosition=this._scrollTop)},_updateScrollerSize(r){const i=this._physicalBottom+Math.max(this._virtualCount-this._physicalCount-this._virtualStart,0)*this._physicalAverage;this._estScrollHeight=i,(r||this._scrollHeight===0||this._scrollPosition>=i-this._physicalSize||Math.abs(i-this._scrollHeight)>=this._viewportHeight)&&(this.$.items.style.height=`${i}px`,this._scrollHeight=i)},scrollToIndex(r){if(typeof r!="number"||r<0||r>this.items.length-1||(he(),this._physicalCount===0))return;r=this._clamp(r,0,this._virtualCount-1),(!this._isIndexRendered(r)||r>=this._maxVirtualStart)&&(this._virtualStart=r-1),this._assignModels(),this._updateMetrics(),this._physicalTop=this._virtualStart*this._physicalAverage;let i=this._physicalStart,e=this._virtualStart,t=0;const s=this._hiddenContentSize;for(;e<r&&t<=s;)t+=this._getPhysicalSizeIncrement(i),i=(i+1)%this._physicalCount,e+=1;this._updateScrollerSize(!0),this._positionItems(),this._resetScrollPosition(this._physicalTop+this._scrollOffset+t),this._increasePoolIfNeeded(0),this._firstVisibleIndexVal=null,this._lastVisibleIndexVal=null},_resetAverage(){this._physicalAverage=0,this._physicalAverageCount=0},_resizeHandler(){this._debounce("_render",()=>{this._firstVisibleIndexVal=null,this._lastVisibleIndexVal=null,this._isVisible?(this.updateViewportBoundaries(),this.toggleScrollListener(!0),this._resetAverage(),this._render()):this.toggleScrollListener(!1)},j)},_isIndexRendered(r){return r>=this._virtualStart&&r<=this._virtualEnd},_getPhysicalIndex(r){return(this._physicalStart+(r-this._virtualStart))%this._physicalCount},_clamp(r,i,e){return Math.min(e,Math.max(i,r))},_debounce(r,i,e){this._debouncers||(this._debouncers={}),this._debouncers[r]=x.debounce(this._debouncers[r],e,i.bind(this)),ii(this._debouncers[r])}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ns=1e5,Oe=1e3;class Ft{constructor({createElements:i,updateElement:e,scrollTarget:t,scrollContainer:s,elementsContainer:n,reorderElements:o}){this.isAttached=!0,this._vidxOffset=0,this.createElements=i,this.updateElement=e,this.scrollTarget=t,this.scrollContainer=s,this.elementsContainer=n||s,this.reorderElements=o,this._maxPages=1.3,this.__placeholderHeight=200,this.__elementHeightQueue=Array(10),this.timeouts={SCROLL_REORDER:500,IGNORE_WHEEL:500,FIX_INVALID_ITEM_POSITIONING:100},this.__resizeObserver=new ResizeObserver(()=>this._resizeHandler()),getComputedStyle(this.scrollTarget).overflow==="visible"&&(this.scrollTarget.style.overflow="auto"),getComputedStyle(this.scrollContainer).position==="static"&&(this.scrollContainer.style.position="relative"),this.__resizeObserver.observe(this.scrollTarget),this.scrollTarget.addEventListener("scroll",()=>this._scrollHandler()),this._scrollLineHeight=this._getScrollLineHeight(),this.scrollTarget.addEventListener("wheel",a=>this.__onWheel(a)),this.reorderElements&&(this.scrollTarget.addEventListener("mousedown",()=>{this.__mouseDown=!0}),this.scrollTarget.addEventListener("mouseup",()=>{this.__mouseDown=!1,this.__pendingReorder&&this.__reorderElements()}))}get scrollOffset(){return 0}get adjustedFirstVisibleIndex(){return this.firstVisibleIndex+this._vidxOffset}get adjustedLastVisibleIndex(){return this.lastVisibleIndex+this._vidxOffset}__hasPlaceholders(){return this.__getVisibleElements().some(i=>i.__virtualizerPlaceholder)}scrollToIndex(i){if(typeof i!="number"||isNaN(i)||this.size===0||!this.scrollTarget.offsetHeight)return;delete this.__pendingScrollToIndex,this._physicalCount<=3&&this.flush(),i=this._clamp(i,0,this.size-1);const e=this.__getVisibleElements().length;let t=Math.floor(i/this.size*this._virtualCount);this._virtualCount-t<e?(t=this._virtualCount-(this.size-i),this._vidxOffset=this.size-this._virtualCount):t<e?i<Oe?(t=i,this._vidxOffset=0):(t=Oe,this._vidxOffset=i-t):this._vidxOffset=i-t,this.__skipNextVirtualIndexAdjust=!0,super.scrollToIndex(t),this.adjustedFirstVisibleIndex!==i&&this._scrollTop<this._maxScrollTop&&!this.grid&&(this._scrollTop-=this.__getIndexScrollOffset(i)||0),this._scrollHandler(),this.__hasPlaceholders()&&(this.__pendingScrollToIndex=i)}flush(){const i=this._physicalCount;this.scrollTarget.offsetHeight!==0&&(this._resizeHandler(),he(),this._scrollHandler(),this.__fixInvalidItemPositioningDebouncer&&this.__fixInvalidItemPositioningDebouncer.flush(),this.__scrollReorderDebouncer&&this.__scrollReorderDebouncer.flush(),this.__debouncerWheelAnimationFrame&&this.__debouncerWheelAnimationFrame.flush(),this._physicalCount!==i&&this.flush())}update(i=0,e=this.size-1){const t=[];this.__getVisibleElements().forEach(s=>{s.__virtualIndex>=i&&s.__virtualIndex<=e&&(this.__updateElement(s,s.__virtualIndex,!0),t.push(s))}),this.__afterElementsUpdated(t)}_updateMetrics(i){he();let e=0,t=0;const s=this._physicalAverageCount,n=this._physicalAverage;this._iterateItems((o,a)=>{t+=this._physicalSizes[o],this._physicalSizes[o]=Math.ceil(this.__getBorderBoxHeight(this._physicalItems[o])),e+=this._physicalSizes[o],this._physicalAverageCount+=this._physicalSizes[o]?1:0},i),this._physicalSize=this._physicalSize+e-t,this._physicalAverageCount!==s&&(this._physicalAverage=Math.round((n*s+e)/this._physicalAverageCount))}__getBorderBoxHeight(i){const e=getComputedStyle(i),t=parseFloat(e.height)||0;if(e.boxSizing==="border-box")return t;const s=parseFloat(e.paddingBottom)||0,n=parseFloat(e.paddingTop)||0,o=parseFloat(e.borderBottomWidth)||0,a=parseFloat(e.borderTopWidth)||0;return t+s+n+o+a}__updateElement(i,e,t){i.__virtualizerPlaceholder&&(i.style.paddingTop="",i.__virtualizerPlaceholder=!1),!this.__preventElementUpdates&&(i.__lastUpdatedIndex!==e||t)&&(this.updateElement(i,e),i.__lastUpdatedIndex=e)}__afterElementsUpdated(i){i.forEach(e=>{const t=e.offsetHeight;if(t===0)e.style.paddingTop=`${this.__placeholderHeight}px`,e.__virtualizerPlaceholder=!0,this.__placeholderClearDebouncer=x.debounce(this.__placeholderClearDebouncer,j,()=>this._resizeHandler());else{this.__elementHeightQueue.push(t),this.__elementHeightQueue.shift();const s=this.__elementHeightQueue.filter(n=>n!==void 0);this.__placeholderHeight=Math.round(s.reduce((n,o)=>n+o,0)/s.length)}}),this.__pendingScrollToIndex!==void 0&&!this.__hasPlaceholders()&&this.scrollToIndex(this.__pendingScrollToIndex)}__getIndexScrollOffset(i){const e=this.__getVisibleElements().find(t=>t.__virtualIndex===i);return e?this.scrollTarget.getBoundingClientRect().top-e.getBoundingClientRect().top:void 0}get size(){return this.__size}set size(i){i!==this.size&&(this.__fixInvalidItemPositioningDebouncer&&this.__fixInvalidItemPositioningDebouncer.cancel(),this._debouncers&&this._debouncers._increasePoolIfNeeded&&this._debouncers._increasePoolIfNeeded.cancel(),this.__size=i,this._physicalItems?(this._updateScrollerSize(),this._virtualCount=this.items.length,this._render()):(this._itemsChanged({path:"items"}),this.__preventElementUpdates=!0,he(),this.__preventElementUpdates=!1),this._isVisible||this._assignModels(),this.elementsContainer.children.length||requestAnimationFrame(()=>this._resizeHandler()),this._resizeHandler(),he())}get _scrollTop(){return this.scrollTarget.scrollTop}set _scrollTop(i){this.scrollTarget.scrollTop=i}get items(){return{length:Math.min(this.size,ns)}}get offsetHeight(){return this.scrollTarget.offsetHeight}get $(){return{items:this.scrollContainer}}updateViewportBoundaries(){const i=window.getComputedStyle(this.scrollTarget);this._scrollerPaddingTop=this.scrollTarget===this?0:parseInt(i["padding-top"],10),this._isRTL=i.direction==="rtl",this._viewportWidth=this.elementsContainer.offsetWidth,this._viewportHeight=this.scrollTarget.offsetHeight,this._scrollPageHeight=this._viewportHeight-this._scrollLineHeight,this.grid&&this._updateGridMetrics()}setAttribute(){}_createPool(i){const e=this.createElements(i),t=document.createDocumentFragment();return e.forEach(s=>{s.style.position="absolute",t.appendChild(s),this.__resizeObserver.observe(s)}),this.elementsContainer.appendChild(t),e}_assignModels(i){const e=[];this._iterateItems((t,s)=>{const n=this._physicalItems[t];n.hidden=s>=this.size,n.hidden?delete n.__lastUpdatedIndex:(n.__virtualIndex=s+(this._vidxOffset||0),this.__updateElement(n,n.__virtualIndex),e.push(n))},i),this.__afterElementsUpdated(e)}_isClientFull(){return setTimeout(()=>{this.__clientFull=!0}),this.__clientFull||super._isClientFull()}translate3d(i,e,t,s){s.style.transform=`translateY(${e})`}toggleScrollListener(){}_scrollHandler(){if(this.scrollTarget.offsetHeight===0)return;this._adjustVirtualIndexOffset(this._scrollTop-(this.__previousScrollTop||0));const i=this.scrollTarget.scrollTop-this._scrollPosition;if(super._scrollHandler(),this._physicalCount!==0){const e=i>=0,t=this._getReusables(!e);t.indexes.length&&(this._physicalTop=t.physicalTop,e?(this._virtualStart-=t.indexes.length,this._physicalStart-=t.indexes.length):(this._virtualStart+=t.indexes.length,this._physicalStart+=t.indexes.length),this._resizeHandler())}i&&(this.__fixInvalidItemPositioningDebouncer=x.debounce(this.__fixInvalidItemPositioningDebouncer,B.after(this.timeouts.FIX_INVALID_ITEM_POSITIONING),()=>this.__fixInvalidItemPositioning())),this.reorderElements&&(this.__scrollReorderDebouncer=x.debounce(this.__scrollReorderDebouncer,B.after(this.timeouts.SCROLL_REORDER),()=>this.__reorderElements())),this.__previousScrollTop=this._scrollTop,this._scrollTop===0&&this.firstVisibleIndex!==0&&Math.abs(i)>0&&this.scrollToIndex(0)}__fixInvalidItemPositioning(){if(!this.scrollTarget.isConnected)return;const i=this._physicalTop>this._scrollTop,e=this._physicalBottom<this._scrollBottom,t=this.adjustedFirstVisibleIndex===0,s=this.adjustedLastVisibleIndex===this.size-1;if(i&&!t||e&&!s){const n=e,o=this._ratio;this._ratio=0,this._scrollPosition=this._scrollTop+(n?-1:1),this._scrollHandler(),this._ratio=o}}__onWheel(i){if(i.ctrlKey||this._hasScrolledAncestor(i.target,i.deltaX,i.deltaY))return;let e=i.deltaY;if(i.deltaMode===WheelEvent.DOM_DELTA_LINE?e*=this._scrollLineHeight:i.deltaMode===WheelEvent.DOM_DELTA_PAGE&&(e*=this._scrollPageHeight),this._deltaYAcc||(this._deltaYAcc=0),this._wheelAnimationFrame){this._deltaYAcc+=e,i.preventDefault();return}e+=this._deltaYAcc,this._deltaYAcc=0,this._wheelAnimationFrame=!0,this.__debouncerWheelAnimationFrame=x.debounce(this.__debouncerWheelAnimationFrame,j,()=>{this._wheelAnimationFrame=!1});const t=Math.abs(i.deltaX)+Math.abs(e);this._canScroll(this.scrollTarget,i.deltaX,e)?(i.preventDefault(),this.scrollTarget.scrollTop+=e,this.scrollTarget.scrollLeft+=i.deltaX,this._hasResidualMomentum=!0,this._ignoreNewWheel=!0,this._debouncerIgnoreNewWheel=x.debounce(this._debouncerIgnoreNewWheel,B.after(this.timeouts.IGNORE_WHEEL),()=>{this._ignoreNewWheel=!1})):this._hasResidualMomentum&&t<=this._previousMomentum||this._ignoreNewWheel?i.preventDefault():t>this._previousMomentum&&(this._hasResidualMomentum=!1),this._previousMomentum=t}_hasScrolledAncestor(i,e,t){if(i===this.scrollTarget||i===this.scrollTarget.getRootNode().host)return!1;if(this._canScroll(i,e,t)&&["auto","scroll"].indexOf(getComputedStyle(i).overflow)!==-1)return!0;if(i!==this&&i.parentElement)return this._hasScrolledAncestor(i.parentElement,e,t)}_canScroll(i,e,t){return t>0&&i.scrollTop<i.scrollHeight-i.offsetHeight||t<0&&i.scrollTop>0||e>0&&i.scrollLeft<i.scrollWidth-i.offsetWidth||e<0&&i.scrollLeft>0}_increasePoolIfNeeded(i){if(this._physicalCount>2&&i){const t=Math.ceil(this._optPhysicalSize/this._physicalAverage)-this._physicalCount;super._increasePoolIfNeeded(Math.max(i,Math.min(100,t)))}else super._increasePoolIfNeeded(i)}_getScrollLineHeight(){const i=document.createElement("div");i.style.fontSize="initial",i.style.display="none",document.body.appendChild(i);const e=window.getComputedStyle(i).fontSize;return document.body.removeChild(i),e?window.parseInt(e):void 0}__getVisibleElements(){return Array.from(this.elementsContainer.children).filter(i=>!i.hidden)}__reorderElements(){if(this.__mouseDown){this.__pendingReorder=!0;return}this.__pendingReorder=!1;const i=this._virtualStart+(this._vidxOffset||0),e=this.__getVisibleElements(),s=e.find(a=>a.contains(this.elementsContainer.getRootNode().activeElement)||a.contains(this.scrollTarget.getRootNode().activeElement))||e[0];if(!s)return;const n=s.__virtualIndex-i,o=e.indexOf(s)-n;if(o>0)for(let a=0;a<o;a++)this.elementsContainer.appendChild(e[a]);else if(o<0)for(let a=e.length+o;a<e.length;a++)this.elementsContainer.insertBefore(e[a],e[0]);if(mt){const{transform:a}=this.scrollTarget.style;this.scrollTarget.style.transform="translateZ(0)",setTimeout(()=>{this.scrollTarget.style.transform=a})}}_adjustVirtualIndexOffset(i){if(this._virtualCount>=this.size)this._vidxOffset=0;else if(this.__skipNextVirtualIndexAdjust)this.__skipNextVirtualIndexAdjust=!1;else if(Math.abs(i)>1e4){const e=this._scrollTop/(this.scrollTarget.scrollHeight-this.scrollTarget.offsetHeight),t=e*this.size;this._vidxOffset=Math.round(t-e*this._virtualCount)}else{const e=this._vidxOffset,t=Oe,s=100;this._scrollTop===0?(this._vidxOffset=0,e!==this._vidxOffset&&super.scrollToIndex(0)):this.firstVisibleIndex<t&&this._vidxOffset>0&&(this._vidxOffset-=Math.min(this._vidxOffset,s),super.scrollToIndex(this.firstVisibleIndex+(e-this._vidxOffset)));const n=this.size-this._virtualCount;this._scrollTop>=this._maxScrollTop&&this._maxScrollTop>0?(this._vidxOffset=n,e!==this._vidxOffset&&super.scrollToIndex(this._virtualCount-1)):this.firstVisibleIndex>this._virtualCount-t&&this._vidxOffset<n&&(this._vidxOffset+=Math.min(n-this._vidxOffset,s),super.scrollToIndex(this.firstVisibleIndex-(this._vidxOffset-e)))}}}Object.setPrototypeOf(Ft.prototype,rs);class os{constructor(i){this.__adapter=new Ft(i)}get firstVisibleIndex(){return this.__adapter.adjustedFirstVisibleIndex}get lastVisibleIndex(){return this.__adapter.adjustedLastVisibleIndex}get size(){return this.__adapter.size}set size(i){this.__adapter.size=i}scrollToIndex(i){this.__adapter.scrollToIndex(i)}update(i=0,e=this.size-1){this.__adapter.update(i,e)}flush(){this.__adapter.flush()}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Ge(r){if(window.Vaadin&&window.Vaadin.templateRendererCallback){window.Vaadin.templateRendererCallback(r);return}r.querySelector("template")&&console.warn(`WARNING: <template> inside <${r.localName}> is no longer supported. Import @vaadin/polymer-legacy-adapter/template-renderer.js to enable compatibility.`)}/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function ne(r){return r.__cells||Array.from(r.querySelectorAll('[part~="cell"]:not([part~="details-cell"])'))}function O(r,i){[...r.children].forEach(i)}function oe(r,i){ne(r).forEach(i),r.__detailsCell&&i(r.__detailsCell)}function Dt(r,i,e){let t=1;r.forEach(s=>{t%10===0&&(t+=1),s._order=e+t*i,t+=1})}function Se(r,i,e){switch(typeof e){case"boolean":r.toggleAttribute(i,e);break;case"string":r.setAttribute(i,e);break;default:r.removeAttribute(i);break}}function Y(r,i,e){i||i===""?gt(r,"part",e):bt(r,"part",e)}function U(r,i,e){r.forEach(t=>{Y(t,e,i)})}function _e(r,i){const e=ne(r);Object.entries(i).forEach(([t,s])=>{Se(r,t,s);const n=`${t}-row`;Y(r,s,n),U(e,`${n}-cell`,s)})}function ot(r,i){const e=ne(r);Object.entries(i).forEach(([t,s])=>{const n=r.getAttribute(t);if(Se(r,t,s),n){const o=`${t}-${n}-row`;Y(r,!1,o),U(e,`${o}-cell`,!1)}if(s){const o=`${t}-${s}-row`;Y(r,s,o),U(e,`${o}-cell`,s)}})}function ee(r,i,e,t,s){Se(r,i,e),s&&Y(r,!1,s),Y(r,e,t||`${i}-cell`)}class q{constructor(i,e){this.__host=i,this.__callback=e,this.__currentSlots=[],this.__onMutation=this.__onMutation.bind(this),this.__observer=new MutationObserver(this.__onMutation),this.__observer.observe(i,{childList:!0}),this.__initialCallDebouncer=x.debounce(this.__initialCallDebouncer,K,()=>this.__onMutation())}disconnect(){this.__observer.disconnect(),this.__initialCallDebouncer.cancel(),this.__toggleSlotChangeListeners(!1)}flush(){this.__onMutation()}__toggleSlotChangeListeners(i){this.__currentSlots.forEach(e=>{i?e.addEventListener("slotchange",this.__onMutation):e.removeEventListener("slotchange",this.__onMutation)})}__onMutation(){const i=!this.__currentColumns;this.__currentColumns||(this.__currentColumns=[]);const e=q.getColumns(this.__host),t=e.filter(a=>!this.__currentColumns.includes(a)),s=this.__currentColumns.filter(a=>!e.includes(a)),n=this.__currentColumns.some((a,d)=>a!==e[d]);this.__currentColumns=e,this.__toggleSlotChangeListeners(!1),this.__currentSlots=[...this.__host.children].filter(a=>a instanceof HTMLSlotElement),this.__toggleSlotChangeListeners(!0),(i||t.length||s.length||n)&&this.__callback(t,s)}static __isColumnElement(i){return i.nodeType===Node.ELEMENT_NODE&&/\bcolumn\b/u.test(i.localName)}static getColumns(i){const e=[],t=i._isColumnElement||q.__isColumnElement;return[...i.children].forEach(s=>{t(s)?e.push(s):s instanceof HTMLSlotElement&&[...s.assignedElements({flatten:!0})].filter(n=>t(n)).forEach(n=>e.push(n))}),e}}/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Mt=r=>class extends r{static get properties(){return{resizable:{type:Boolean,sync:!0,value(){if(this.localName==="vaadin-grid-column-group")return;const e=this.parentNode;return e&&e.localName==="vaadin-grid-column-group"&&e.resizable||!1}},frozen:{type:Boolean,value:!1,sync:!0},frozenToEnd:{type:Boolean,value:!1,sync:!0},rowHeader:{type:Boolean,value:!1,sync:!0},hidden:{type:Boolean,value:!1,sync:!0},header:{type:String,sync:!0},textAlign:{type:String,sync:!0},headerPartName:{type:String,sync:!0},footerPartName:{type:String,sync:!0},_lastFrozen:{type:Boolean,value:!1,sync:!0},_bodyContentHidden:{type:Boolean,value:!1,sync:!0},_firstFrozenToEnd:{type:Boolean,value:!1,sync:!0},_order:{type:Number,sync:!0},_reorderStatus:{type:Boolean,sync:!0},_emptyCells:Array,_headerCell:Object,_footerCell:Object,_grid:Object,__initialized:{type:Boolean,value:!0},headerRenderer:{type:Function,sync:!0},_headerRenderer:{type:Function,computed:"_computeHeaderRenderer(headerRenderer, header, __initialized)",sync:!0},footerRenderer:{type:Function,sync:!0},_footerRenderer:{type:Function,computed:"_computeFooterRenderer(footerRenderer, __initialized)",sync:!0},__gridColumnElement:{type:Boolean,value:!0}}}static get observers(){return["_widthChanged(width, _headerCell, _footerCell, _cells)","_frozenChanged(frozen, _headerCell, _footerCell, _cells)","_frozenToEndChanged(frozenToEnd, _headerCell, _footerCell, _cells)","_flexGrowChanged(flexGrow, _headerCell, _footerCell, _cells)","_textAlignChanged(textAlign, _cells, _headerCell, _footerCell)","_orderChanged(_order, _headerCell, _footerCell, _cells)","_lastFrozenChanged(_lastFrozen)","_firstFrozenToEndChanged(_firstFrozenToEnd)","_onRendererOrBindingChanged(_renderer, _cells, _bodyContentHidden, path)","_onHeaderRendererOrBindingChanged(_headerRenderer, _headerCell, path, header)","_onFooterRendererOrBindingChanged(_footerRenderer, _footerCell)","_resizableChanged(resizable, _headerCell)","_reorderStatusChanged(_reorderStatus, _headerCell, _footerCell, _cells)","_hiddenChanged(hidden, _headerCell, _footerCell, _cells)","_rowHeaderChanged(rowHeader, _cells)","__headerFooterPartNameChanged(_headerCell, _footerCell, headerPartName, footerPartName)"]}get _grid(){return this._gridValue||(this._gridValue=this._findHostGrid()),this._gridValue}get _allCells(){return[].concat(this._cells||[]).concat(this._emptyCells||[]).concat(this._headerCell).concat(this._footerCell).filter(e=>e)}connectedCallback(){super.connectedCallback(),requestAnimationFrame(()=>{this._grid&&this._allCells.forEach(e=>{e._content.parentNode||this._grid.appendChild(e._content)})})}disconnectedCallback(){super.disconnectedCallback(),requestAnimationFrame(()=>{this._grid||this._allCells.forEach(e=>{e._content.parentNode&&e._content.parentNode.removeChild(e._content)})}),this._gridValue=void 0}ready(){super.ready(),Ge(this)}_findHostGrid(){let e=this;for(;e&&!/^vaadin.*grid(-pro)?$/u.test(e.localName);)e=e.assignedSlot?e.assignedSlot.parentNode:e.parentNode;return e||void 0}_renderHeaderAndFooter(){this._renderHeaderCellContent(this._headerRenderer,this._headerCell),this._renderFooterCellContent(this._footerRenderer,this._footerCell)}_flexGrowChanged(e){this.parentElement&&this.parentElement._columnPropChanged&&this.parentElement._columnPropChanged("flexGrow"),this._allCells.forEach(t=>{t.style.flexGrow=e})}_orderChanged(e){this._allCells.forEach(t=>{t.style.order=e})}_widthChanged(e){this.parentElement&&this.parentElement._columnPropChanged&&this.parentElement._columnPropChanged("width"),this._allCells.forEach(t=>{t.style.width=e})}_frozenChanged(e){this.parentElement&&this.parentElement._columnPropChanged&&this.parentElement._columnPropChanged("frozen",e),this._allCells.forEach(t=>{ee(t,"frozen",e)}),this._grid&&this._grid._frozenCellsChanged&&this._grid._frozenCellsChanged()}_frozenToEndChanged(e){this.parentElement&&this.parentElement._columnPropChanged&&this.parentElement._columnPropChanged("frozenToEnd",e),this._allCells.forEach(t=>{this._grid&&t.parentElement===this._grid.$.sizer||ee(t,"frozen-to-end",e)}),this._grid&&this._grid._frozenCellsChanged&&this._grid._frozenCellsChanged()}_lastFrozenChanged(e){this._allCells.forEach(t=>{ee(t,"last-frozen",e)}),this.parentElement&&this.parentElement._columnPropChanged&&(this.parentElement._lastFrozen=e)}_firstFrozenToEndChanged(e){this._allCells.forEach(t=>{this._grid&&t.parentElement===this._grid.$.sizer||ee(t,"first-frozen-to-end",e)}),this.parentElement&&this.parentElement._columnPropChanged&&(this.parentElement._firstFrozenToEnd=e)}_rowHeaderChanged(e,t){t&&t.forEach(s=>{s.setAttribute("role",e?"rowheader":"gridcell")})}_generateHeader(e){return e.substr(e.lastIndexOf(".")+1).replace(/([A-Z])/gu,"-$1").toLowerCase().replace(/-/gu," ").replace(/^./u,t=>t.toUpperCase())}_reorderStatusChanged(e){const t=this.__previousReorderStatus,s=t?`reorder-${t}-cell`:"",n=`reorder-${e}-cell`;this._allCells.forEach(o=>{ee(o,"reorder-status",e,n,s)}),this.__previousReorderStatus=e}_resizableChanged(e,t){e===void 0||t===void 0||t&&[t].concat(this._emptyCells).forEach(s=>{if(s){const n=s.querySelector('[part~="resize-handle"]');if(n&&s.removeChild(n),e){const o=document.createElement("div");o.setAttribute("part","resize-handle"),s.appendChild(o)}}})}_textAlignChanged(e){if(e===void 0||this._grid===void 0)return;if(["start","end","center"].indexOf(e)===-1){console.warn('textAlign can only be set as "start", "end" or "center"');return}let t;getComputedStyle(this._grid).direction==="ltr"?e==="start"?t="left":e==="end"&&(t="right"):e==="start"?t="right":e==="end"&&(t="left"),this._allCells.forEach(s=>{s._content.style.textAlign=e,getComputedStyle(s._content).textAlign!==e&&(s._content.style.textAlign=t)})}_hiddenChanged(e){this.parentElement&&this.parentElement._columnPropChanged&&this.parentElement._columnPropChanged("hidden",e),!!e!=!!this._previousHidden&&this._grid&&(e===!0&&this._allCells.forEach(t=>{t._content.parentNode&&t._content.parentNode.removeChild(t._content)}),this._grid._debouncerHiddenChanged=x.debounce(this._grid._debouncerHiddenChanged,j,()=>{this._grid&&this._grid._renderColumnTree&&this._grid._renderColumnTree(this._grid._columnTree)}),this._grid._debounceUpdateFrozenColumn&&this._grid._debounceUpdateFrozenColumn(),this._grid._resetKeyboardNavigation&&this._grid._resetKeyboardNavigation()),this._previousHidden=e}_runRenderer(e,t,s){const n=[t._content,this];s&&s.item&&n.push(s),e.apply(this,n)}__renderCellsContent(e,t){this.hidden||!this._grid||t.forEach(s=>{if(!s.parentElement)return;const n=this._grid.__getRowModel(s.parentElement);e&&(s._renderer!==e&&this._clearCellContent(s),s._renderer=e,(n.item||e===this._headerRenderer||e===this._footerRenderer)&&this._runRenderer(e,s,n))})}_clearCellContent(e){e._content.innerHTML="",delete e._content._$litPart$}_renderHeaderCellContent(e,t){!t||!e||(this.__renderCellsContent(e,[t]),this._grid&&t.parentElement&&this._grid.__debounceUpdateHeaderFooterRowVisibility(t.parentElement))}_onHeaderRendererOrBindingChanged(e,t,...s){this._renderHeaderCellContent(e,t)}__headerFooterPartNameChanged(e,t,s,n){[{cell:e,partName:s},{cell:t,partName:n}].forEach(({cell:o,partName:a})=>{if(o){const d=o.__customParts||[];o.part.remove(...d),o.__customParts=a?a.trim().split(" "):[],o.part.add(...o.__customParts)}})}_renderBodyCellsContent(e,t){!t||!e||this.__renderCellsContent(e,t)}_onRendererOrBindingChanged(e,t,...s){this._renderBodyCellsContent(e,t)}_renderFooterCellContent(e,t){!t||!e||(this.__renderCellsContent(e,[t]),this._grid&&t.parentElement&&this._grid.__debounceUpdateHeaderFooterRowVisibility(t.parentElement))}_onFooterRendererOrBindingChanged(e,t){this._renderFooterCellContent(e,t)}__setTextContent(e,t){e.textContent!==t&&(e.textContent=t)}__textHeaderRenderer(){this.__setTextContent(this._headerCell._content,this.header)}_defaultHeaderRenderer(){this.path&&this.__setTextContent(this._headerCell._content,this._generateHeader(this.path))}_defaultRenderer(e,t,{item:s}){this.path&&this.__setTextContent(e,Ve(this.path,s))}_defaultFooterRenderer(){}_computeHeaderRenderer(e,t){return e||(t!=null?this.__textHeaderRenderer:this._defaultHeaderRenderer)}_computeRenderer(e){return e||this._defaultRenderer}_computeFooterRenderer(e){return e||this._defaultFooterRenderer}},as=r=>class extends Mt(pe(r)){static get properties(){return{width:{type:String,value:"100px",sync:!0},flexGrow:{type:Number,value:1,sync:!0},renderer:{type:Function,sync:!0},_renderer:{type:Function,computed:"_computeRenderer(renderer, __initialized)",sync:!0},path:{type:String,sync:!0},autoWidth:{type:Boolean,value:!1},_focusButtonMode:{type:Boolean,value:!1},_cells:{type:Array,sync:!0}}}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Lt extends as(L){static get is(){return"vaadin-grid-column"}}V(Lt);/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ls=r=>class extends r{static get properties(){return{width:{type:String,value:"58px",sync:!0},flexGrow:{type:Number,value:0,sync:!0},selectAll:{type:Boolean,value:!1,notify:!0,sync:!0},autoSelect:{type:Boolean,value:!1,sync:!0},dragSelect:{type:Boolean,value:!1,sync:!0},_indeterminate:{type:Boolean,sync:!0},_selectAllHidden:Boolean}}static get observers(){return["_onHeaderRendererOrBindingChanged(_headerRenderer, _headerCell, path, header, selectAll, _indeterminate, _selectAllHidden)"]}_defaultHeaderRenderer(e,t){let s=e.firstElementChild;s||(s=document.createElement("vaadin-checkbox"),s.setAttribute("aria-label","Select All"),s.classList.add("vaadin-grid-select-all-checkbox"),e.appendChild(s),s.addEventListener("checked-changed",this.__onSelectAllCheckedChanged.bind(this)));const n=this.__isChecked(this.selectAll,this._indeterminate);s.__rendererChecked=n,s.checked=n,s.hidden=this._selectAllHidden,s.indeterminate=this._indeterminate}_defaultRenderer(e,t,{item:s,selected:n}){let o=e.firstElementChild;o||(o=document.createElement("vaadin-checkbox"),o.setAttribute("aria-label","Select Row"),e.appendChild(o),o.addEventListener("checked-changed",this.__onSelectRowCheckedChanged.bind(this)),me(e,"track",this.__onCellTrack.bind(this)),e.addEventListener("mousedown",this.__onCellMouseDown.bind(this)),e.addEventListener("click",this.__onCellClick.bind(this))),o.__item=s,o.__rendererChecked=n,o.checked=n}__onSelectAllCheckedChanged(e){e.target.checked!==e.target.__rendererChecked&&(this._indeterminate||e.target.checked?this._selectAll():this._deselectAll())}__onSelectRowCheckedChanged(e){e.target.checked!==e.target.__rendererChecked&&(e.target.checked?this._selectItem(e.target.__item):this._deselectItem(e.target.__item))}__onCellTrack(e){if(this.dragSelect)if(this.__dragCurrentY=e.detail.y,this.__dragDy=e.detail.dy,e.detail.state==="start"){const s=this._grid._getRenderedRows().find(n=>n.contains(e.currentTarget.assignedSlot));this.__selectOnDrag=!this._grid._isSelected(s._item),this.__dragStartIndex=s.index,this.__dragStartItem=s._item,this.__dragAutoScroller()}else e.detail.state==="end"&&(this.__dragStartItem&&(this.__selectOnDrag?this._selectItem(this.__dragStartItem):this._deselectItem(this.__dragStartItem)),setTimeout(()=>{this.__dragStartIndex=void 0}))}__onCellMouseDown(e){this.dragSelect&&e.preventDefault()}__onCellClick(e){this.__dragStartIndex!==void 0&&e.preventDefault()}__dragAutoScroller(){if(this.__dragStartIndex===void 0)return;const e=this._grid._getRenderedRows(),t=e.find(d=>{const _=d.getBoundingClientRect();return this.__dragCurrentY>=_.top&&this.__dragCurrentY<=_.bottom});let s=t?t.index:void 0;const n=this.__getScrollableArea();this.__dragCurrentY<n.top?s=this._grid._firstVisibleIndex:this.__dragCurrentY>n.bottom&&(s=this._grid._lastVisibleIndex),s!==void 0&&e.forEach(d=>{(s>this.__dragStartIndex&&d.index>=this.__dragStartIndex&&d.index<=s||s<this.__dragStartIndex&&d.index<=this.__dragStartIndex&&d.index>=s)&&(this.__selectOnDrag?this._selectItem(d._item):this._deselectItem(d._item),this.__dragStartItem=void 0)});const o=n.height*.15,a=10;if(this.__dragDy<0&&this.__dragCurrentY<n.top+o){const d=n.top+o-this.__dragCurrentY,_=Math.min(1,d/o);this._grid.$.table.scrollTop-=_*a}if(this.__dragDy>0&&this.__dragCurrentY>n.bottom-o){const d=this.__dragCurrentY-(n.bottom-o),_=Math.min(1,d/o);this._grid.$.table.scrollTop+=_*a}setTimeout(()=>this.__dragAutoScroller(),10)}__getScrollableArea(){const e=this._grid.$.table.getBoundingClientRect(),t=this._grid.$.header.getBoundingClientRect(),s=this._grid.$.footer.getBoundingClientRect();return{top:e.top+t.height,bottom:e.bottom-s.height,left:e.left,right:e.right,height:e.height-t.height-s.height,width:e.width}}_selectAll(){}_deselectAll(){}_selectItem(e){}_deselectItem(e){}__isChecked(e,t){return t||e}};class at extends ls(Lt){static get is(){return"vaadin-grid-flow-selection-column"}static get properties(){return{autoWidth:{type:Boolean,value:!0},width:{type:String,value:"56px"}}}_defaultHeaderRenderer(i,e){super._defaultHeaderRenderer(i,e);const t=i.firstElementChild;t&&(t.id="selectAllCheckbox")}_selectAll(){this.selectAll=!0,this.$server.selectAll()}_deselectAll(){this.selectAll=!1,this.$server.deselectAll()}_selectItem(i){this._grid.$connector.doSelection([i],!0)}_deselectItem(i){this._grid.$connector.doDeselection([i],!0),this.selectAll=!1}}customElements.define(at.is,at);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ve=new ResizeObserver(r=>{setTimeout(()=>{r.forEach(i=>{i.target.resizables?i.target.resizables.forEach(e=>{e._onResize(i.contentRect)}):i.target._onResize(i.contentRect)})})}),ds=Ie(r=>class extends r{get _observeParent(){return!1}connectedCallback(){if(super.connectedCallback(),ve.observe(this),this._observeParent){const e=this.parentNode instanceof ShadowRoot?this.parentNode.host:this.parentNode;e.resizables||(e.resizables=new Set,ve.observe(e)),e.resizables.add(this),this.__parent=e}}disconnectedCallback(){super.disconnectedCallback(),ve.unobserve(this);const e=this.__parent;if(this._observeParent&&e){const t=e.resizables;t&&(t.delete(this),t.size===0&&ve.unobserve(e)),this.__parent=null}}_onResize(e){}}),hs=F`
  /* :hover needed to workaround https://github.com/vaadin/web-components/issues/3133 */
  :host(:hover) {
    user-select: none;
    -ms-user-select: none;
    -webkit-user-select: none;
  }

  :host([role='menuitem'][menu-item-checked]) [part='checkmark']::before {
    opacity: 1;
  }

  :host([aria-haspopup='true'])::after {
    font-family: lumo-icons;
    font-size: var(--lumo-icon-size-xs);
    content: var(--lumo-icons-angle-right);
    color: var(--lumo-tertiary-text-color);
  }

  :host(:not([dir='rtl'])[aria-haspopup='true'])::after {
    margin-right: calc(var(--lumo-space-m) * -1);
    padding-left: var(--lumo-space-m);
  }

  :host([expanded]) {
    background-color: var(--lumo-primary-color-10pct);
  }

  /* RTL styles */
  :host([dir='rtl'][aria-haspopup='true'])::after {
    content: var(--lumo-icons-angle-left);
    margin-left: calc(var(--lumo-space-m) * -1);
    padding-right: var(--lumo-space-m);
  }
`;k("vaadin-context-menu-item",[Rt,hs],{moduleId:"lumo-context-menu-item"});const $t=F`
  :host {
    -webkit-tap-highlight-color: transparent;
    --_lumo-item-selected-icon-display: var(--_lumo-list-box-item-selected-icon-display, block);
  }

  /* Dividers */
  [part='items'] ::slotted(hr) {
    height: 1px;
    border: 0;
    padding: 0;
    margin: var(--lumo-space-s) var(--lumo-border-radius-m);
    background-color: var(--lumo-contrast-10pct);
  }
`;k("vaadin-list-box",$t,{moduleId:"lumo-list-box"});const cs=F`
  :host {
    --_lumo-list-box-item-selected-icon-display: block;
  }

  /* Normal item */
  [part='items'] ::slotted([role='menuitem']) {
    -webkit-tap-highlight-color: var(--lumo-primary-color-10pct);
    cursor: default;
    outline: none;
    border-radius: var(--lumo-border-radius-m);
    padding-left: calc(var(--lumo-border-radius-m) / 4);
    padding-right: calc(var(--lumo-space-l) + var(--lumo-border-radius-m) / 4);
  }

  /* Hovered item */
  /* TODO a workaround until we have "focus-follows-mouse". After that, use the hover style for focus-ring as well */
  [part='items'] ::slotted([role='menuitem']:hover:not([disabled])),
  [part='items'] ::slotted([role='menuitem'][expanded]:not([disabled])) {
    background-color: var(--lumo-primary-color-10pct);
  }

  /* RTL styles */
  :host([dir='rtl']) [part='items'] ::slotted([role='menuitem']) {
    padding-left: calc(var(--lumo-space-l) + var(--lumo-border-radius-m) / 4);
    padding-right: calc(var(--lumo-border-radius-m) / 4);
  }

  /* Focused item */
  @media (pointer: coarse) {
    [part='items'] ::slotted([role='menuitem']:hover:not([expanded]):not([disabled])) {
      background-color: transparent;
    }
  }
`;k("vaadin-context-menu-list-box",[$t,cs],{moduleId:"lumo-context-menu-list-box"});const us=F`
  :host([phone]) {
    /* stylelint-disable declaration-block-no-redundant-longhand-properties */
    top: 0 !important;
    right: 0 !important;
    bottom: var(--vaadin-overlay-viewport-bottom) !important;
    left: 0 !important;
    /* stylelint-enable declaration-block-no-redundant-longhand-properties */
    align-items: stretch;
    justify-content: flex-end;
  }

  /* TODO These style overrides should not be needed.
   We should instead offer a way to have non-selectable items inside the context menu. */

  :host {
    --_lumo-list-box-item-selected-icon-display: none;
    --_lumo-list-box-item-padding-left: calc(var(--lumo-space-m) + var(--lumo-border-radius-m) / 4);
  }

  [part='overlay'] {
    outline: none;
  }
`;k("vaadin-context-menu-overlay",[kt,us],{moduleId:"lumo-context-menu-overlay"});/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */ri({name:"vaadin-contextmenu",deps:["touchstart","touchmove","touchend","contextmenu"],flow:{start:["touchstart","contextmenu"],end:["contextmenu"]},emits:["vaadin-contextmenu"],info:{sourceEvent:null},reset(){this.info.sourceEvent=null,this._cancelTimer(),this.info.touchJob=null,this.info.touchStartCoords=null},_cancelTimer(){this._timerId&&(clearTimeout(this._timerId),delete this._fired)},_setSourceEvent(r){this.info.sourceEvent=r;const i=r.composedPath();this.info.sourceEvent.__composedPath=i},touchstart(r){this._setSourceEvent(r),this.info.touchStartCoords={x:r.changedTouches[0].clientX,y:r.changedTouches[0].clientY};const i=r.composedPath()[0]||r.target;this._timerId=setTimeout(()=>{const e=r.changedTouches[0];r.shiftKey||(ke&&(this._fired=!0,this.fire(i,e.clientX,e.clientY)),Qe("tap"))},500)},touchmove(r){const e=this.info.touchStartCoords;(Math.abs(e.x-r.changedTouches[0].clientX)>15||Math.abs(e.y-r.changedTouches[0].clientY)>15)&&this._cancelTimer()},touchend(r){this._fired&&r.preventDefault(),this._cancelTimer()},contextmenu(r){r.shiftKey||(this._setSourceEvent(r),this.fire(r.target,r.clientX,r.clientY),Qe("tap"))},fire(r,i,e){const t=this.info.sourceEvent,s=new Event("vaadin-contextmenu",{bubbles:!0,cancelable:!0,composed:!0});s.detail={x:i,y:e,sourceEvent:t},r.dispatchEvent(s),s.defaultPrevented&&t&&t.preventDefault&&t.preventDefault()}});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const _s=r=>class extends vt(ni(r)){static get properties(){return{_hasVaadinItemMixin:{value:!0},selected:{type:Boolean,value:!1,reflectToAttribute:!0,observer:"_selectedChanged"},_value:String}}get _activeKeys(){return["Enter"," "]}get value(){return this._value!==void 0?this._value:this.textContent.trim()}set value(e){this._value=e}ready(){super.ready();const e=this.getAttribute("value");e!==null&&(this.value=e)}focus(){this.disabled||(super.focus(),this._setFocused(!0))}_shouldSetActive(e){return!this.disabled&&!(e.type==="keydown"&&e.defaultPrevented)}_selectedChanged(e){this.setAttribute("aria-selected",e)}_disabledChanged(e){super._disabledChanged(e),e&&(this.selected=!1,this.blur())}_onKeyDown(e){super._onKeyDown(e),this._activeKeys.includes(e.key)&&!e.defaultPrevented&&(e.preventDefault(),this.click())}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class fs extends _s(ae(pe(L))){static get is(){return"vaadin-context-menu-item"}static get template(){return Q`
      <style>
        :host {
          display: inline-block;
        }

        :host([hidden]) {
          display: none !important;
        }
      </style>
      <span part="checkmark" aria-hidden="true"></span>
      <div part="content">
        <slot></slot>
      </div>
    `}ready(){super.ready(),this.setAttribute("role","menuitem")}}V(fs);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Le(r,i){const{scrollLeft:e}=r;return i!=="rtl"?e:r.scrollWidth-r.clientWidth+e}function ps(r,i,e){i!=="rtl"?r.scrollLeft=e:r.scrollLeft=r.clientWidth-r.scrollWidth+e}/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ms=r=>class extends oi(r){get focused(){return(this._getItems()||[]).find(ai)}get _vertical(){return!0}focus(){const e=this._getItems();if(Array.isArray(e)){const t=this._getAvailableIndex(e,0,null,s=>!re(s));t>=0&&e[t].focus()}}_getItems(){return Array.from(this.children)}_onKeyDown(e){if(super._onKeyDown(e),e.metaKey||e.ctrlKey)return;const{key:t}=e,s=this._getItems()||[],n=s.indexOf(this.focused);let o,a;const _=!this._vertical&&this.getAttribute("dir")==="rtl"?-1:1;this.__isPrevKey(t)?(a=-_,o=n-_):this.__isNextKey(t)?(a=_,o=n+_):t==="Home"?(a=1,o=0):t==="End"&&(a=-1,o=s.length-1),o=this._getAvailableIndex(s,o,a,f=>!re(f)),o>=0&&(e.preventDefault(),this._focus(o,!0))}__isPrevKey(e){return this._vertical?e==="ArrowUp":e==="ArrowLeft"}__isNextKey(e){return this._vertical?e==="ArrowDown":e==="ArrowRight"}_focus(e,t=!1){const s=this._getItems();this._focusItem(s[e],t)}_focusItem(e){e&&(e.focus(),e.setAttribute("focus-ring",""))}_getAvailableIndex(e,t,s,n){const o=e.length;let a=t;for(let d=0;typeof a=="number"&&d<o;d+=1,a+=s||1){a<0?a=o-1:a>=o&&(a=0);const _=e[a];if(!_.hasAttribute("disabled")&&this.__isMatchingItem(_,n))return a}return-1}__isMatchingItem(e,t){return typeof t=="function"?t(e):!0}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const gs=r=>class extends ms(r){static get properties(){return{disabled:{type:Boolean,value:!1,reflectToAttribute:!0},selected:{type:Number,reflectToAttribute:!0,notify:!0},orientation:{type:String,reflectToAttribute:!0,value:""},items:{type:Array,readOnly:!0,notify:!0},_searchBuf:{type:String,value:""}}}static get observers(){return["_enhanceItems(items, orientation, selected, disabled)"]}get _isRTL(){return!this._vertical&&this.getAttribute("dir")==="rtl"}get _scrollerElement(){return console.warn(`Please implement the '_scrollerElement' property in <${this.localName}>`),this}get _vertical(){return this.orientation!=="horizontal"}focus(){this._observer&&this._observer.flush();const e=this.querySelector('[tabindex="0"]')||(this.items?this.items[0]:null);this._focusItem(e)}ready(){super.ready(),this.addEventListener("click",t=>this._onClick(t));const e=this.shadowRoot.querySelector("slot:not([name])");this._observer=new li(e,()=>{this._setItems(this._filterItems(di(this)))})}_getItems(){return this.items}_enhanceItems(e,t,s,n){if(!n&&e){this.setAttribute("aria-orientation",t||"vertical"),e.forEach(a=>{t?a.setAttribute("orientation",t):a.removeAttribute("orientation")}),this._setFocusable(s||0);const o=e[s];e.forEach(a=>{a.selected=a===o}),o&&!o.disabled&&this._scrollToItem(s)}}_filterItems(e){return e.filter(t=>t._hasVaadinItemMixin)}_onClick(e){if(e.metaKey||e.shiftKey||e.ctrlKey||e.defaultPrevented)return;const t=this._filterItems(e.composedPath())[0];let s;t&&!t.disabled&&(s=this.items.indexOf(t))>=0&&(this.selected=s)}_searchKey(e,t){this._searchReset=x.debounce(this._searchReset,B.after(500),()=>{this._searchBuf=""}),this._searchBuf+=t.toLowerCase(),this.items.some(n=>this.__isMatchingKey(n))||(this._searchBuf=t.toLowerCase());const s=this._searchBuf.length===1?e+1:e;return this._getAvailableIndex(this.items,s,1,n=>this.__isMatchingKey(n)&&getComputedStyle(n).display!=="none")}__isMatchingKey(e){return e.textContent.replace(/[^\p{L}\p{Nd}]/gu,"").toLowerCase().startsWith(this._searchBuf)}_onKeyDown(e){if(e.metaKey||e.ctrlKey)return;const t=e.key,s=this.items.indexOf(this.focused);if(/[\p{L}\p{Nd}]/u.test(t)&&t.length===1){const n=this._searchKey(s,t);n>=0&&this._focus(n);return}super._onKeyDown(e)}_isItemHidden(e){return getComputedStyle(e).display==="none"}_setFocusable(e){e=this._getAvailableIndex(this.items,e,1);const t=this.items[e];this.items.forEach(s=>{s.tabIndex=s===t?0:-1})}_focus(e){this.items.forEach((t,s)=>{t.focused=s===e}),this._setFocusable(e),this._scrollToItem(e),super._focus(e)}_scrollToItem(e){const t=this.items[e];if(!t)return;const s=this._vertical?["top","bottom"]:this._isRTL?["right","left"]:["left","right"],n=this._scrollerElement.getBoundingClientRect(),o=(this.items[e+1]||t).getBoundingClientRect(),a=(this.items[e-1]||t).getBoundingClientRect();let d=0;!this._isRTL&&o[s[1]]>=n[s[1]]||this._isRTL&&o[s[1]]<=n[s[1]]?d=o[s[1]]-n[s[1]]:(!this._isRTL&&a[s[0]]<=n[s[0]]||this._isRTL&&a[s[0]]>=n[s[0]])&&(d=a[s[0]]-n[s[0]]),this._scroll(d)}_scroll(e){if(this._vertical)this._scrollerElement.scrollTop+=e;else{const t=this.getAttribute("dir")||"ltr",s=Le(this._scrollerElement,t)+e;ps(this._scrollerElement,t,s)}}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class bs extends gs(ae(pe($e(L)))){static get is(){return"vaadin-context-menu-list-box"}static get template(){return Q`
      <style>
        :host {
          display: flex;
        }

        :host([hidden]) {
          display: none !important;
        }

        [part='items'] {
          height: 100%;
          width: 100%;
          overflow-y: auto;
          -webkit-overflow-scrolling: touch;
        }
      </style>
      <div part="items">
        <slot></slot>
      </div>
    `}static get properties(){return{orientation:{readOnly:!0}}}get _scrollerElement(){return this.shadowRoot.querySelector('[part="items"]')}ready(){super.ready(),this.setAttribute("role","menu")}}V(bs);/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const vs=r=>class extends hi(ci(r)){static get properties(){return{parentOverlay:{type:Object,readOnly:!0},_theme:{type:String,readOnly:!0,sync:!0}}}static get observers(){return["_themeChanged(_theme)"]}ready(){super.ready(),this.restoreFocusOnClose=!0,this.addEventListener("keydown",e=>{if(!e.defaultPrevented&&e.composedPath()[0]===this.$.overlay&&[38,40].indexOf(e.keyCode)>-1){const t=this.getFirstChild();t&&Array.isArray(t.items)&&t.items.length&&(e.preventDefault(),e.keyCode===38?t.items[t.items.length-1].focus():t.focus())}})}getFirstChild(){return this.querySelector(":not(style):not(slot)")}_themeChanged(){this.close()}getBoundaries(){const e=this.getBoundingClientRect(),t=this.$.overlay.getBoundingClientRect();let s=e.bottom-t.height;const n=this.parentOverlay;if(n&&n.hasAttribute("bottom-aligned")){const o=getComputedStyle(n);s=s-parseFloat(o.bottom)-parseFloat(o.height)}return{xMax:e.right-t.width,xMin:e.left+t.width,yMax:s}}_updatePosition(){if(super._updatePosition(),this.positionTarget&&this.parentOverlay){const e=this.$.content,t=getComputedStyle(e);!!this.style.left?this.style.left=`${parseFloat(this.style.left)+parseFloat(t.paddingLeft)}px`:this.style.right=`${parseFloat(this.style.right)+parseFloat(t.paddingRight)}px`,!!this.style.bottom?this.style.bottom=`${parseFloat(this.style.bottom)-parseFloat(t.paddingBottom)}px`:this.style.top=`${parseFloat(this.style.top)-parseFloat(t.paddingTop)}px`}}_shouldRestoreFocus(){return this.parentOverlay?!1:super._shouldRestoreFocus()}_deepContains(e){let t=Ct(this.localName,e);for(;t;){if(t===this)return!0;t=t.parentOverlay}return!1}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Cs=F`
  :host {
    align-items: flex-start;
    justify-content: flex-start;
  }

  :host([right-aligned]),
  :host([end-aligned]) {
    align-items: flex-end;
  }

  :host([bottom-aligned]) {
    justify-content: flex-end;
  }

  [part='overlay'] {
    background-color: #fff;
  }

  @media (forced-colors: active) {
    [part='overlay'] {
      outline: 3px solid !important;
    }
  }
`;/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */k("vaadin-context-menu-overlay",[ui,Cs],{moduleId:"vaadin-context-menu-overlay-styles"});class ys extends vs(_i(pe(ae(L)))){static get is(){return"vaadin-context-menu-overlay"}static get template(){return Q`
      <div id="backdrop" part="backdrop" hidden$="[[!withBackdrop]]"></div>
      <div part="overlay" id="overlay" tabindex="0">
        <div part="content" id="content">
          <slot></slot>
        </div>
      </div>
    `}}V(ys);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class xs{constructor(i,e){this.query=i,this.callback=e,this._boundQueryHandler=this._queryHandler.bind(this)}hostConnected(){this._removeListener(),this._mediaQuery=window.matchMedia(this.query),this._addListener(),this._queryHandler(this._mediaQuery)}hostDisconnected(){this._removeListener()}_addListener(){this._mediaQuery&&this._mediaQuery.addListener(this._boundQueryHandler)}_removeListener(){this._mediaQuery&&this._mediaQuery.removeListener(this._boundQueryHandler),this._mediaQuery=null}_queryHandler(i){typeof this.callback=="function"&&this.callback(i.matches)}}/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ws=r=>class extends r{static get properties(){return{items:{type:Array,sync:!0}}}constructor(){super(),this.__itemsOutsideClickListener=e=>{e.composedPath().some(t=>t.localName===`${this._tagNamePrefix}-overlay`)||this.dispatchEvent(new CustomEvent("items-outside-click"))},this.addEventListener("items-outside-click",()=>{this.items&&this.close()})}get _tagNamePrefix(){return"vaadin-context-menu"}connectedCallback(){super.connectedCallback(),document.documentElement.addEventListener("click",this.__itemsOutsideClickListener)}disconnectedCallback(){super.disconnectedCallback(),document.documentElement.removeEventListener("click",this.__itemsOutsideClickListener)}__forwardFocus(){const e=this._overlayElement,t=e.getFirstChild();if(e.parentOverlay){const s=e.parentOverlay.querySelector("[expanded]");s&&s.hasAttribute("focused")&&t?t.focus():e.$.overlay.focus()}else t&&t.focus()}__openSubMenu(e,t,s){e.items=t._item.children,e.listenOn=t,e.overlayClass=s;const n=this._overlayElement,o=e._overlayElement;o.positionTarget=t,o.noHorizontalOverlap=!0,o._setParentOverlay(n),n.hasAttribute("theme")?e.setAttribute("theme",n.getAttribute("theme")):e.removeAttribute("theme");const a=o.$.content;a.style.minWidth="",t.dispatchEvent(new CustomEvent("opensubmenu",{detail:{children:t._item.children}}))}__createComponent(e){let t;return e.component instanceof HTMLElement?t=e.component:t=document.createElement(e.component||`${this._tagNamePrefix}-item`),t._hasVaadinItemMixin&&t.setAttribute("role","menuitem"),t.localName==="hr"?t.setAttribute("role","separator"):t.setAttribute("aria-haspopup","false"),this._setMenuItemTheme(t,e,this._theme),t._item=e,e.text&&(t.textContent=e.text),e.className&&t.setAttribute("class",e.className),this.__toggleMenuComponentAttribute(t,"menu-item-checked",e.checked),this.__toggleMenuComponentAttribute(t,"disabled",e.disabled),e.children&&e.children.length&&(this.__updateExpanded(t,!1),t.setAttribute("aria-haspopup","true")),t}__initListBox(){const e=document.createElement(`${this._tagNamePrefix}-list-box`);return this._theme&&e.setAttribute("theme",this._theme),e.addEventListener("selected-changed",t=>{const{value:s}=t.detail;if(typeof s=="number"){const n=e.items[s]._item;e.selected=null,n.children||this.dispatchEvent(new CustomEvent("item-selected",{detail:{value:n}}))}}),e}__initOverlay(){const e=this._overlayElement;e.$.backdrop.addEventListener("click",()=>{this.close()}),e.addEventListener(Ee?"click":"mouseover",t=>{this.__showSubMenu(t)}),e.addEventListener("keydown",t=>{const{key:s}=t,n=this.__isRTL,o=s==="ArrowRight",a=s==="ArrowLeft";!n&&o||n&&a||s==="Enter"||s===" "?this.__showSubMenu(t):!n&&a||n&&o||s==="Escape"?(s==="Escape"&&t.stopPropagation(),this.close(),this.listenOn.focus()):s==="Tab"&&this.dispatchEvent(new CustomEvent("close-all-menus"))})}__initSubMenu(){const e=document.createElement(this.constructor.is);return e._modeless=!0,e.openOn="opensubmenu",e.setAttribute("hidden",""),this.addEventListener("opened-changed",t=>{t.detail.value||this._subMenu.close()}),e.addEventListener("close-all-menus",()=>{this.dispatchEvent(new CustomEvent("close-all-menus"))}),e.addEventListener("item-selected",t=>{const{detail:s}=t;this.dispatchEvent(new CustomEvent("item-selected",{detail:s}))}),this.addEventListener("close-all-menus",()=>{this._overlayElement.close()}),this.addEventListener("item-selected",t=>{const s=t.target,n=t.detail.value,o=s.items.indexOf(n);n.keepOpen&&o>-1?(s._overlayElement.requestContentUpdate(),s._listBox._observer.flush(),s._listBox.children[o].focus()):n.keepOpen||this.close()}),e.addEventListener("opened-changed",t=>{if(!t.detail.value){const s=this._listBox.querySelector("[expanded]");s&&this.__updateExpanded(s,!1)}}),e}__showSubMenu(e,t=e.composedPath().find(s=>s.localName===`${this._tagNamePrefix}-item`)){if(!this.__openListenerActive)return;if(this._overlayElement.hasAttribute("opening")){requestAnimationFrame(()=>{this.__showSubMenu(e,t)});return}const s=this._subMenu;if(t){const{children:n}=t._item;if(s.items!==n&&s.close(),!this.opened)return;if(n&&n.length){this.__updateExpanded(t,!0);const{overlayClass:o}=this;this.__openSubMenu(s,t,o)}else s.listenOn.focus()}}__itemsRenderer(e,t,{detail:s}){this.__initMenu(e,t);const n=e.querySelector(this.constructor.is);n.closeOn=t.closeOn;const o=e.querySelector(`${this._tagNamePrefix}-list-box`);o.innerHTML="",[...s.children||t.items].forEach(a=>{const d=this.__createComponent(a);o.appendChild(d)})}_setMenuItemTheme(e,t,s){let n=e.getAttribute("theme")||s;t.theme!=null&&(n=Array.isArray(t.theme)?t.theme.join(" "):t.theme),this.__updateTheme(e,n)}__toggleMenuComponentAttribute(e,t,s){s?(e.setAttribute(t,""),e[`__has-${t}`]=!0):e[`__has-${t}`]&&(e.removeAttribute(t),e[`__has-${t}`]=!1)}__initMenu(e,t){if(e.firstElementChild)this.__updateTheme(this._listBox,this._theme);else{this.__initOverlay();const s=this.__initListBox();this._listBox=s,e.appendChild(s);const n=this.__initSubMenu();this._subMenu=n,e.appendChild(n),requestAnimationFrame(()=>{this.__openListenerActive=!0})}}__updateExpanded(e,t){e.setAttribute("aria-expanded",t.toString()),e.toggleAttribute("expanded",t)}__updateTheme(e,t){t?e.setAttribute("theme",t):e.removeAttribute("theme")}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const As=r=>class extends ws(r){static get properties(){return{selector:{type:String},opened:{type:Boolean,value:!1,notify:!0,readOnly:!0},openOn:{type:String,value:"vaadin-contextmenu",sync:!0},listenOn:{type:Object,sync:!0,value(){return this}},closeOn:{type:String,value:"click",observer:"_closeOnChanged",sync:!0},renderer:{type:Function,sync:!0},_modeless:{type:Boolean,sync:!0},_context:{type:Object,sync:!0},_phone:{type:Boolean},_touch:{type:Boolean,value:Ee},_wide:{type:Boolean},_wideMediaQuery:{type:String,value:"(min-device-width: 750px)"}}}static get observers(){return["_openedChanged(opened)","_targetOrOpenOnChanged(listenOn, openOn)","_rendererChanged(renderer, items)","_touchOrWideChanged(_touch, _wide)","_overlayContextChanged(_overlayElement, _context)","_overlayModelessChanged(_overlayElement, _modeless)","_overlayPhoneChanged(_overlayElement, _phone)","_overlayThemeChanged(_overlayElement, _theme)"]}constructor(){super(),this._createOverlay(),this._boundOpen=this.open.bind(this),this._boundClose=this.close.bind(this),this._boundPreventDefault=this._preventDefault.bind(this),this._boundOnGlobalContextMenu=this._onGlobalContextMenu.bind(this)}connectedCallback(){super.connectedCallback(),this.__boundOnScroll=this.__onScroll.bind(this),window.addEventListener("scroll",this.__boundOnScroll,!0),this.__restoreOpened&&this._setOpened(!0)}disconnectedCallback(){super.disconnectedCallback(),window.removeEventListener("scroll",this.__boundOnScroll,!0),this.__restoreOpened=this.opened,this.close()}ready(){super.ready(),this.addController(new xs(this._wideMediaQuery,e=>{this._wide=e}))}_createOverlay(){const e=document.createElement(`${this._tagNamePrefix}-overlay`);e.owner=this,e.addEventListener("opened-changed",t=>{this._onOverlayOpened(t)}),e.addEventListener("vaadin-overlay-open",t=>{this._onVaadinOverlayOpen(t)}),this._overlayElement=e}_onOverlayOpened(e){this._setOpened(e.detail.value),this.__alignOverlayPosition()}_onVaadinOverlayOpen(){this.__alignOverlayPosition(),this._overlayElement.style.opacity="",this.__forwardFocus()}_overlayContextChanged(e,t){e&&(e.model=t)}_overlayModelessChanged(e,t){e&&(e.modeless=t)}_overlayPhoneChanged(e,t){e&&(e.toggleAttribute("phone",t),e.withBackdrop=t)}_overlayThemeChanged(e,t){e&&(t?e.setAttribute("theme",t):e.removeAttribute("theme"))}_targetOrOpenOnChanged(e,t){this._oldListenOn&&this._oldOpenOn&&(this._unlisten(this._oldListenOn,this._oldOpenOn,this._boundOpen),this._oldListenOn.style.webkitTouchCallout="",this._oldListenOn.style.webkitUserSelect="",this._oldListenOn.style.userSelect="",this._oldListenOn=null,this._oldOpenOn=null),e&&t&&(this._listen(e,t,this._boundOpen),this._oldListenOn=e,this._oldOpenOn=t)}_touchOrWideChanged(e,t){this._phone=!t&&e}_setListenOnUserSelect(e){this.listenOn.style.webkitTouchCallout=e,this.listenOn.style.webkitUserSelect=e,this.listenOn.style.userSelect=e,document.getSelection().removeAllRanges()}_closeOnChanged(e,t){const s="vaadin-overlay-outside-click",n=this._overlayElement;t&&this._unlisten(n,t,this._boundClose),e?(this._listen(n,e,this._boundClose),n.removeEventListener(s,this._boundPreventDefault)):n.addEventListener(s,this._boundPreventDefault)}_preventDefault(e){e.preventDefault()}_openedChanged(e){e?(document.documentElement.addEventListener("contextmenu",this._boundOnGlobalContextMenu,!0),this._setListenOnUserSelect("none")):(document.documentElement.removeEventListener("contextmenu",this._boundOnGlobalContextMenu,!0),this._setListenOnUserSelect("")),this._overlayElement.opened=e}requestContentUpdate(){!this._overlayElement||!this.renderer||this._overlayElement.requestContentUpdate()}_rendererChanged(e,t){if(t){if(e)throw new Error("The items API cannot be used together with a renderer");this.closeOn==="click"&&(this.closeOn=""),e=this.__itemsRenderer}this._overlayElement.renderer=e}close(){this._setOpened(!1)}_contextTarget(e){if(this.selector){const t=this.listenOn.querySelectorAll(this.selector);return Array.prototype.filter.call(t,s=>e.composedPath().indexOf(s)>-1)[0]}return e.target}open(e){e&&!this.opened&&(this._context={detail:e.detail,target:this._contextTarget(e)},this._context.target&&(e.preventDefault(),e.stopPropagation(),this.__x=this._getEventCoordinate(e,"x"),this.__pageXOffset=window.pageXOffset,this.__y=this._getEventCoordinate(e,"y"),this.__pageYOffset=window.pageYOffset,this._overlayElement.style.opacity="0",this._setOpened(!0)))}__onScroll(){if(!this.opened)return;const e=window.pageYOffset-this.__pageYOffset,t=window.pageXOffset-this.__pageXOffset;this.__adjustPosition("left",-t),this.__adjustPosition("right",t),this.__adjustPosition("top",-e),this.__adjustPosition("bottom",e),this.__pageYOffset+=e,this.__pageXOffset+=t}__adjustPosition(e,t){const n=this._overlayElement.style;n[e]=`${(parseInt(n[e])||0)+t}px`}__alignOverlayPosition(){const e=this._overlayElement;if(e.positionTarget)return;const t=e.style;["top","right","bottom","left"].forEach(p=>t.removeProperty(p)),["right-aligned","end-aligned","bottom-aligned"].forEach(p=>e.removeAttribute(p));const{xMax:s,xMin:n,yMax:o}=e.getBoundaries(),a=this.__x,d=this.__y,_=document.documentElement.clientWidth,f=document.documentElement.clientHeight;this.__isRTL?a>_/2||a>n?t.right=`${Math.max(0,_-a)}px`:(t.left=`${a}px`,this._setEndAligned(e)):a<_/2||a<s?t.left=`${a}px`:(t.right=`${Math.max(0,_-a)}px`,this._setEndAligned(e)),d<f/2||d<o?t.top=`${d}px`:(t.bottom=`${Math.max(0,f-d)}px`,e.setAttribute("bottom-aligned",""))}_setEndAligned(e){e.setAttribute("end-aligned",""),this.__isRTL||e.setAttribute("right-aligned","")}_getEventCoordinate(e,t){if(e.detail instanceof Object){if(e.detail[t])return e.detail[t];if(e.detail.sourceEvent)return this._getEventCoordinate(e.detail.sourceEvent,t)}else{const s=`client${t.toUpperCase()}`,n=e.changedTouches?e.changedTouches[0][s]:e[s];if(n===0){const o=e.target.getBoundingClientRect();return t==="x"?o.left:o.top+o.height}return n}}_listen(e,t,s){Ce[t]?me(e,t,s):e.addEventListener(t,s)}_unlisten(e,t,s){Ce[t]?yt(e,t,s):e.removeEventListener(t,s)}_onGlobalContextMenu(e){e.shiftKey||(e.preventDefault(),this.close())}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Is extends As(fi($e(He(pi(L))))){static get template(){return Q`
      <style>
        :host {
          display: block;
        }

        :host([hidden]) {
          display: none !important;
        }
      </style>

      <slot id="slot"></slot>
    `}static get is(){return"vaadin-context-menu"}ready(){super.ready(),Ge(this)}_attachDom(i){const e=this.attachShadow({mode:"open"});return e.appendChild(i),e.appendChild(this._overlayElement),e}}V(Is);(function(){function r(a){return window.Vaadin.Flow.tryCatchWrapper(a,"Vaadin Context Menu")}function i(a,d){try{return window.Vaadin.Flow.clients[a].getByNodeId(d)}catch(_){console.error("Could not get node %s from app %s",d,a),console.error(_)}}function e(a,d){a.$connector||(a.$connector={generateItems:r(_=>{const f=t(d,_);a.items=f})})}function t(a,d){const _=i(a,d);if(_)return Array.from(_.children).map(f=>{const p={component:f,checked:f._checked,keepOpen:f._keepOpen,className:f.className,theme:f.__theme};return f._hasVaadinItemMixin&&f._containerNodeId&&(p.children=t(a,f._containerNodeId)),f._item=p,p})}function s(a,d){a._item&&(a._item.checked=d,a._item.keepOpen&&a.toggleAttribute("menu-item-checked",d))}function n(a,d){a._item&&(a._item.keepOpen=d)}function o(a,d){a._item&&(a._item.theme=d)}window.Vaadin.Flow.contextMenuConnector={initLazy(...a){return r(e)(...a)},generateItemsTree(...a){return r(t)(...a)},setChecked(...a){return r(s)(...a)},setKeepOpen(...a){return r(n)(...a)},setTheme(...a){return r(o)(...a)}}})();(function(){function r(e){return window.Vaadin.Flow.tryCatchWrapper(e,"Vaadin Context Menu Target")}function i(e){e.$contextMenuTargetConnector||(e.$contextMenuTargetConnector={openOnHandler:r(function(t){t.preventDefault(),t.stopPropagation(),this.$contextMenuTargetConnector.openEvent=t;let s={};e.getContextMenuBeforeOpenDetail&&(s=e.getContextMenuBeforeOpenDetail(t)),e.dispatchEvent(new CustomEvent("vaadin-context-menu-before-open",{detail:s}))}),updateOpenOn:r(function(t){this.removeListener(),this.openOnEventType=t,customElements.whenDefined("vaadin-context-menu").then(r(()=>{Ce[t]?me(e,t,this.openOnHandler):e.addEventListener(t,this.openOnHandler)}))}),removeListener:r(function(){this.openOnEventType&&(Ce[this.openOnEventType]?yt(e,this.openOnEventType,this.openOnHandler):e.removeEventListener(this.openOnEventType,this.openOnHandler))}),openMenu:r(function(t){t.open(this.openEvent)}),removeConnector:r(function(){this.removeListener(),e.$contextMenuTargetConnector=void 0})})}window.Vaadin.Flow.contextMenuTargetConnector={init(...e){return r(i)(...e)}}})();k("vaadin-checkbox",F`
    :host {
      color: var(--vaadin-checkbox-label-color, var(--lumo-body-text-color));
      font-size: var(--vaadin-checkbox-label-font-size, var(--lumo-font-size-m));
      font-family: var(--lumo-font-family);
      line-height: var(--lumo-line-height-s);
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      -webkit-tap-highlight-color: transparent;
      -webkit-user-select: none;
      -moz-user-select: none;
      user-select: none;
      cursor: default;
      outline: none;
      --_checkbox-size: var(--vaadin-checkbox-size, calc(var(--lumo-size-m) / 2));
      --_focus-ring-color: var(--vaadin-focus-ring-color, var(--lumo-primary-color-50pct));
      --_focus-ring-width: var(--vaadin-focus-ring-width, 2px);
      --_selection-color: var(--vaadin-selection-color, var(--lumo-primary-color));
    }

    :host([has-label]) ::slotted(label) {
      padding: var(
        --vaadin-checkbox-label-padding,
        var(--lumo-space-xs) var(--lumo-space-s) var(--lumo-space-xs) var(--lumo-space-xs)
      );
    }

    [part='checkbox'] {
      width: var(--_checkbox-size);
      height: var(--_checkbox-size);
      margin: var(--lumo-space-xs);
      position: relative;
      border-radius: var(--vaadin-checkbox-border-radius, var(--lumo-border-radius-s));
      background: var(--vaadin-checkbox-background, var(--lumo-contrast-20pct));
      transition: transform 0.2s cubic-bezier(0.12, 0.32, 0.54, 2), background-color 0.15s;
      cursor: var(--lumo-clickable-cursor);
      /* Default field border color */
      --_input-border-color: var(--vaadin-input-field-border-color, var(--lumo-contrast-50pct));
    }

    :host([indeterminate]),
    :host([checked]) {
      --vaadin-input-field-border-color: transparent;
    }

    :host([indeterminate]) [part='checkbox'],
    :host([checked]) [part='checkbox'] {
      background-color: var(--_selection-color);
    }

    /* Checkmark */
    [part='checkbox']::after {
      pointer-events: none;
      font-family: 'lumo-icons';
      content: var(--vaadin-checkbox-checkmark-char, var(--lumo-icons-checkmark));
      color: var(--vaadin-checkbox-checkmark-color, var(--lumo-primary-contrast-color));
      font-size: var(--vaadin-checkbox-checkmark-size, calc(var(--_checkbox-size) + 2px));
      line-height: 1;
      position: absolute;
      top: -1px;
      left: -1px;
      contain: content;
      opacity: 0;
    }

    :host([checked]) [part='checkbox']::after {
      opacity: 1;
    }

    /* Indeterminate checkmark */
    :host([indeterminate]) [part='checkbox']::after {
      content: var(--vaadin-checkbox-checkmark-char-indeterminate, '');
      opacity: 1;
      top: 45%;
      height: 10%;
      left: 22%;
      right: 22%;
      width: auto;
      border: 0;
      background-color: var(--lumo-primary-contrast-color);
    }

    /* Focus ring */
    :host([focus-ring]) [part='checkbox'] {
      box-shadow: 0 0 0 1px var(--lumo-base-color), 0 0 0 calc(var(--_focus-ring-width) + 1px) var(--_focus-ring-color),
        inset 0 0 0 var(--_input-border-width, 0) var(--_input-border-color);
    }

    /* Disabled */
    :host([disabled]) {
      pointer-events: none;
      color: var(--lumo-disabled-text-color);
      --vaadin-input-field-border-color: var(--lumo-contrast-20pct);
    }

    :host([disabled]) ::slotted(label) {
      color: inherit;
    }

    :host([disabled]) [part='checkbox'] {
      background-color: var(--lumo-contrast-10pct);
    }

    :host([disabled]) [part='checkbox']::after {
      color: var(--lumo-contrast-30pct);
    }

    :host([indeterminate][disabled]) [part='checkbox']::after {
      background-color: var(--lumo-contrast-30pct);
    }

    /* RTL specific styles */
    :host([dir='rtl'][has-label]) ::slotted(label) {
      padding: var(--lumo-space-xs) var(--lumo-space-xs) var(--lumo-space-xs) var(--lumo-space-s);
    }

    /* Used for activation "halo" */
    [part='checkbox']::before {
      pointer-events: none;
      color: transparent;
      width: 100%;
      height: 100%;
      line-height: var(--_checkbox-size);
      border-radius: inherit;
      background-color: inherit;
      transform: scale(1.4);
      opacity: 0;
      transition: transform 0.1s, opacity 0.8s;
    }

    /* Hover */
    :host(:not([checked]):not([indeterminate]):not([disabled]):hover) [part='checkbox'] {
      background: var(--vaadin-checkbox-background-hover, var(--lumo-contrast-30pct));
    }

    /* Disable hover for touch devices */
    @media (pointer: coarse) {
      :host(:not([checked]):not([indeterminate]):not([disabled]):hover) [part='checkbox'] {
        background: var(--vaadin-checkbox-background, var(--lumo-contrast-20pct));
      }
    }

    /* Active */
    :host([active]) [part='checkbox'] {
      transform: scale(0.9);
      transition-duration: 0.05s;
    }

    :host([active][checked]) [part='checkbox'] {
      transform: scale(1.1);
    }

    :host([active]:not([checked])) [part='checkbox']::before {
      transition-duration: 0.01s, 0.01s;
      transform: scale(0);
      opacity: 0.4;
    }
  `,{moduleId:"lumo-checkbox"});k("vaadin-grid",F`
    :host {
      font-family: var(--lumo-font-family);
      font-size: var(--lumo-font-size-m);
      line-height: var(--lumo-line-height-s);
      color: var(--lumo-body-text-color);
      background-color: var(--lumo-base-color);
      box-sizing: border-box;
      -webkit-text-size-adjust: 100%;
      -webkit-tap-highlight-color: transparent;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
      --_focus-ring-color: var(--vaadin-focus-ring-color, var(--lumo-primary-color-50pct));
      --_focus-ring-width: var(--vaadin-focus-ring-width, 2px);
      /* For internal use only */
      --_lumo-grid-border-color: var(--lumo-contrast-20pct);
      --_lumo-grid-secondary-border-color: var(--lumo-contrast-10pct);
      --_lumo-grid-border-width: 1px;
      --_lumo-grid-selected-row-color: var(--lumo-primary-color-10pct);
    }

    /* No (outer) border */

    :host(:not([theme~='no-border'])) {
      border: var(--_lumo-grid-border-width) solid var(--_lumo-grid-border-color);
    }

    :host([disabled]) {
      opacity: 0.7;
    }

    /* Cell styles */

    [part~='cell'] {
      min-height: var(--lumo-size-m);
      background-color: var(--vaadin-grid-cell-background, var(--lumo-base-color));
      cursor: default;
      --_cell-padding: var(--vaadin-grid-cell-padding, var(--_cell-default-padding));
      --_cell-default-padding: var(--lumo-space-xs) var(--lumo-space-m);
    }

    [part~='cell'] ::slotted(vaadin-grid-cell-content) {
      cursor: inherit;
      padding: var(--_cell-padding);
    }

    /* Apply row borders by default and introduce the "no-row-borders" variant */
    :host(:not([theme~='no-row-borders'])) [part~='cell']:not([part~='details-cell']) {
      border-top: var(--_lumo-grid-border-width) solid var(--_lumo-grid-secondary-border-color);
    }

    /* Hide first body row top border */
    :host(:not([theme~='no-row-borders'])) [part~='first-row'] [part~='cell']:not([part~='details-cell']) {
      border-top: 0;
      min-height: calc(var(--lumo-size-m) - var(--_lumo-grid-border-width));
    }

    /* Focus-ring */

    [part~='row'] {
      position: relative;
    }

    [part~='row']:focus,
    [part~='focused-cell']:focus {
      outline: none;
    }

    :host([navigating]) [part~='row']:focus::before,
    :host([navigating]) [part~='focused-cell']:focus::before {
      content: '';
      position: absolute;
      inset: 0;
      pointer-events: none;
      box-shadow: inset 0 0 0 var(--_focus-ring-width) var(--_focus-ring-color);
    }

    :host([navigating]) [part~='row']:focus::before {
      transform: translateX(calc(-1 * var(--_grid-horizontal-scroll-position)));
      z-index: 3;
    }

    /* Drag and Drop styles */
    :host([dragover])::after {
      content: '';
      position: absolute;
      z-index: 100;
      inset: 0;
      pointer-events: none;
      box-shadow: inset 0 0 0 var(--_focus-ring-width) var(--_focus-ring-color);
    }

    [part~='row'][dragover] {
      z-index: 100 !important;
    }

    [part~='row'][dragover] [part~='cell'] {
      overflow: visible;
    }

    [part~='row'][dragover] [part~='cell']::after {
      content: '';
      position: absolute;
      inset: 0;
      height: calc(var(--_lumo-grid-border-width) + 2px);
      pointer-events: none;
      background: var(--lumo-primary-color-50pct);
    }

    [part~='row'][dragover] [part~='cell'][last-frozen]::after {
      right: -1px;
    }

    :host([theme~='no-row-borders']) [dragover] [part~='cell']::after {
      height: 2px;
    }

    [part~='row'][dragover='below'] [part~='cell']::after {
      top: 100%;
      bottom: auto;
      margin-top: -1px;
    }

    :host([all-rows-visible]) [part~='last-row'][dragover='below'] [part~='cell']::after {
      height: 1px;
    }

    [part~='row'][dragover='above'] [part~='cell']::after {
      top: auto;
      bottom: 100%;
      margin-bottom: -1px;
    }

    [part~='row'][details-opened][dragover='below'] [part~='cell']:not([part~='details-cell'])::after,
    [part~='row'][details-opened][dragover='above'] [part~='details-cell']::after {
      display: none;
    }

    [part~='row'][dragover][dragover='on-top'] [part~='cell']::after {
      height: 100%;
      opacity: 0.5;
    }

    [part~='row'][dragstart] [part~='cell'] {
      border: none !important;
      box-shadow: none !important;
    }

    [part~='row'][dragstart] [part~='cell'][last-column] {
      border-radius: 0 var(--lumo-border-radius-s) var(--lumo-border-radius-s) 0;
    }

    [part~='row'][dragstart] [part~='cell'][first-column] {
      border-radius: var(--lumo-border-radius-s) 0 0 var(--lumo-border-radius-s);
    }

    #scroller [part~='row'][dragstart]:not([dragstart=''])::after {
      display: block;
      position: absolute;
      left: var(--_grid-drag-start-x);
      top: var(--_grid-drag-start-y);
      z-index: 100;
      content: attr(dragstart);
      align-items: center;
      justify-content: center;
      box-sizing: border-box;
      padding: calc(var(--lumo-space-xs) * 0.8);
      color: var(--lumo-error-contrast-color);
      background-color: var(--lumo-error-color);
      border-radius: var(--lumo-border-radius-m);
      font-family: var(--lumo-font-family);
      font-size: var(--lumo-font-size-xxs);
      line-height: 1;
      font-weight: 500;
      text-transform: initial;
      letter-spacing: initial;
      min-width: calc(var(--lumo-size-s) * 0.7);
      text-align: center;
    }

    /* Headers and footers */

    [part~='header-cell'],
    [part~='footer-cell'],
    [part~='reorder-ghost'] {
      font-size: var(--lumo-font-size-s);
      font-weight: 500;
    }

    [part~='footer-cell'] {
      font-weight: 400;
    }

    [part~='row']:only-child [part~='header-cell'] {
      min-height: var(--lumo-size-xl);
    }

    /* Header borders */

    /* Hide first header row top border */
    :host(:not([theme~='no-row-borders'])) [part~='row']:first-child [part~='header-cell'] {
      border-top: 0;
    }

    [part~='row']:last-child [part~='header-cell'] {
      border-bottom: var(--_lumo-grid-border-width) solid transparent;
    }

    :host(:not([theme~='no-row-borders'])) [part~='row']:last-child [part~='header-cell'] {
      border-bottom-color: var(--_lumo-grid-secondary-border-color);
    }

    /* Overflow uses a stronger border color */
    :host([overflow~='top']) [part~='row']:last-child [part~='header-cell'] {
      border-bottom-color: var(--_lumo-grid-border-color);
    }

    /* Footer borders */

    [part~='row']:first-child [part~='footer-cell'] {
      border-top: var(--_lumo-grid-border-width) solid transparent;
    }

    :host(:not([theme~='no-row-borders'])) [part~='row']:first-child [part~='footer-cell'] {
      border-top-color: var(--_lumo-grid-secondary-border-color);
    }

    /* Overflow uses a stronger border color */
    :host([overflow~='bottom']) [part~='row']:first-child [part~='footer-cell'] {
      border-top-color: var(--_lumo-grid-border-color);
    }

    /* Column reordering */

    :host([reordering]) [part~='cell'] {
      background: linear-gradient(var(--lumo-shade-20pct), var(--lumo-shade-20pct)) var(--lumo-base-color);
    }

    :host([reordering]) [part~='cell'][reorder-status='allowed'] {
      background: var(--lumo-base-color);
    }

    :host([reordering]) [part~='cell'][reorder-status='dragging'] {
      background: linear-gradient(var(--lumo-contrast-5pct), var(--lumo-contrast-5pct)) var(--lumo-base-color);
    }

    [part~='reorder-ghost'] {
      opacity: 0.85;
      box-shadow: var(--lumo-box-shadow-s);
      /* TODO Use the same styles as for the cell element (reorder-ghost copies styles from the cell element) */
      padding: var(--lumo-space-s) var(--lumo-space-m) !important;
    }

    /* Column resizing */

    [part='resize-handle'] {
      width: 3px;
      background-color: var(--lumo-primary-color-50pct);
      opacity: 0;
      transition: opacity 0.2s;
    }

    :host(:not([reordering])) *:not([column-resizing]) [part~='cell']:hover [part='resize-handle'],
    [part='resize-handle']:active {
      opacity: 1;
      transition-delay: 0.15s;
    }

    /* Column borders */

    :host([theme~='column-borders']) [part~='cell']:not([last-column]):not([part~='details-cell']) {
      border-right: var(--_lumo-grid-border-width) solid var(--_lumo-grid-secondary-border-color);
    }

    /* Frozen columns */

    [last-frozen] {
      border-right: var(--_lumo-grid-border-width) solid transparent;
      overflow: hidden;
    }

    :host([overflow~='start']) [part~='cell'][last-frozen]:not([part~='details-cell']) {
      border-right-color: var(--_lumo-grid-border-color);
    }

    [first-frozen-to-end] {
      border-left: var(--_lumo-grid-border-width) solid transparent;
    }

    :host([overflow~='end']) [part~='cell'][first-frozen-to-end]:not([part~='details-cell']) {
      border-left-color: var(--_lumo-grid-border-color);
    }

    /* Row stripes */

    :host([theme~='row-stripes']) [part~='even-row'] [part~='body-cell'],
    :host([theme~='row-stripes']) [part~='even-row'] [part~='details-cell'] {
      background-image: linear-gradient(var(--lumo-contrast-5pct), var(--lumo-contrast-5pct));
      background-repeat: repeat-x;
    }

    /* Selected row */

    /* Raise the selected rows above unselected rows (so that box-shadow can cover unselected rows) */
    :host(:not([reordering])) [part~='row'][selected] {
      z-index: 1;
    }

    :host(:not([reordering])) [part~='row'][selected] [part~='body-cell']:not([part~='details-cell']) {
      background-image: linear-gradient(var(--_lumo-grid-selected-row-color), var(--_lumo-grid-selected-row-color));
      background-repeat: repeat;
    }

    /* Cover the border of an unselected row */
    :host(:not([theme~='no-row-borders'])) [part~='row'][selected] [part~='cell']:not([part~='details-cell']) {
      box-shadow: 0 var(--_lumo-grid-border-width) 0 0 var(--_lumo-grid-selected-row-color);
    }

    /* Compact */

    :host([theme~='compact']) [part~='row']:only-child [part~='header-cell'] {
      min-height: var(--lumo-size-m);
    }

    :host([theme~='compact']) [part~='cell'] {
      min-height: var(--lumo-size-s);
      --_cell-default-padding: var(--lumo-space-xs) var(--lumo-space-s);
    }

    :host([theme~='compact']) [part~='first-row'] [part~='cell']:not([part~='details-cell']) {
      min-height: calc(var(--lumo-size-s) - var(--_lumo-grid-border-width));
    }

    /* Wrap cell contents */

    :host([theme~='wrap-cell-content']) [part~='cell'] ::slotted(vaadin-grid-cell-content) {
      white-space: normal;
    }

    /* RTL specific styles */

    :host([dir='rtl']) [part~='row'][dragstart] [part~='cell'][last-column] {
      border-radius: var(--lumo-border-radius-s) 0 0 var(--lumo-border-radius-s);
    }

    :host([dir='rtl']) [part~='row'][dragstart] [part~='cell'][first-column] {
      border-radius: 0 var(--lumo-border-radius-s) var(--lumo-border-radius-s) 0;
    }

    :host([dir='rtl'][theme~='column-borders']) [part~='cell']:not([last-column]):not([part~='details-cell']) {
      border-right: none;
      border-left: var(--_lumo-grid-border-width) solid var(--_lumo-grid-secondary-border-color);
    }

    :host([dir='rtl']) [last-frozen] {
      border-right: none;
      border-left: var(--_lumo-grid-border-width) solid transparent;
    }

    :host([dir='rtl']) [first-frozen-to-end] {
      border-left: none;
      border-right: var(--_lumo-grid-border-width) solid transparent;
    }

    :host([dir='rtl'][overflow~='start']) [part~='cell'][last-frozen]:not([part~='details-cell']) {
      border-left-color: var(--_lumo-grid-border-color);
    }

    :host([dir='rtl'][overflow~='end']) [part~='cell'][first-frozen-to-end]:not([part~='details-cell']) {
      border-right-color: var(--_lumo-grid-border-color);
    }
  `,{moduleId:"lumo-grid"});/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Es=r=>class extends r{static get observers(){return["_a11yUpdateGridSize(size, _columnTree)"]}_a11yGetHeaderRowCount(e){return e.filter(t=>t.some(s=>s.headerRenderer||s.path||s.header)).length}_a11yGetFooterRowCount(e){return e.filter(t=>t.some(s=>s.headerRenderer)).length}_a11yUpdateGridSize(e,t){if(e===void 0||t===void 0)return;const s=t[t.length-1];this.$.table.setAttribute("aria-rowcount",e+this._a11yGetHeaderRowCount(t)+this._a11yGetFooterRowCount(t)),this.$.table.setAttribute("aria-colcount",s&&s.length||0),this._a11yUpdateHeaderRows(),this._a11yUpdateFooterRows()}_a11yUpdateHeaderRows(){O(this.$.header,(e,t)=>{e.setAttribute("aria-rowindex",t+1)})}_a11yUpdateFooterRows(){O(this.$.footer,(e,t)=>{e.setAttribute("aria-rowindex",this._a11yGetHeaderRowCount(this._columnTree)+this.size+t+1)})}_a11yUpdateRowRowindex(e,t){e.setAttribute("aria-rowindex",t+this._a11yGetHeaderRowCount(this._columnTree)+1)}_a11yUpdateRowSelected(e,t){e.setAttribute("aria-selected",!!t),oe(e,s=>{s.setAttribute("aria-selected",!!t)})}_a11yUpdateRowExpanded(e){this.__isRowExpandable(e)?e.setAttribute("aria-expanded","false"):this.__isRowCollapsible(e)?e.setAttribute("aria-expanded","true"):e.removeAttribute("aria-expanded")}_a11yUpdateRowLevel(e,t){t>0||this.__isRowCollapsible(e)||this.__isRowExpandable(e)?e.setAttribute("aria-level",t+1):e.removeAttribute("aria-level")}_a11ySetRowDetailsCell(e,t){oe(e,s=>{s!==t&&s.setAttribute("aria-controls",t.id)})}_a11yUpdateCellColspan(e,t){e.setAttribute("aria-colspan",Number(t))}_a11yUpdateSorters(){Array.from(this.querySelectorAll("vaadin-grid-sorter")).forEach(e=>{let t=e.parentNode;for(;t&&t.localName!=="vaadin-grid-cell-content";)t=t.parentNode;t&&t.assignedSlot&&t.assignedSlot.parentNode.setAttribute("aria-sort",{asc:"ascending",desc:"descending"}[String(e.direction)]||"none")})}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ht=r=>{if(!r.parentNode)return!1;const e=Array.from(r.parentNode.querySelectorAll("[tabindex], button, input, select, textarea, object, iframe, a[href], area[href]")).filter(t=>{const s=t.getAttribute("part");return!(s&&s.includes("body-cell"))}).includes(r);return!r.disabled&&e&&r.offsetParent&&getComputedStyle(r).visibility!=="hidden"},Ss=r=>class extends r{static get properties(){return{activeItem:{type:Object,notify:!0,value:null,sync:!0}}}ready(){super.ready(),this.$.scroller.addEventListener("click",this._onClick.bind(this)),this.addEventListener("cell-activate",this._activateItem.bind(this)),this.addEventListener("row-activate",this._activateItem.bind(this))}_activateItem(e){const t=e.detail.model,s=t?t.item:null;s&&(this.activeItem=this._itemsEqual(this.activeItem,s)?null:s)}_onClick(e){if(e.defaultPrevented)return;const t=e.composedPath(),s=t[t.indexOf(this.$.table)-3];if(!s||s.getAttribute("part").indexOf("details-cell")>-1)return;const n=s._content,o=this.getRootNode().activeElement;!n.contains(o)&&!this._isFocusable(e.target)&&!(e.target instanceof HTMLLabelElement)&&this.dispatchEvent(new CustomEvent("cell-activate",{detail:{model:this.__getRowModel(s.parentElement)}}))}_isFocusable(e){return Ht(e)}};function se(r,i){return r.split(".").reduce((e,t)=>e[t],i)}function lt(r,i,e){if(e.length===0)return!1;let t=!0;return r.forEach(({path:s})=>{if(!s||s.indexOf(".")===-1)return;const n=s.replace(/\.[^.]*$/u,"");se(n,e[0])===void 0&&(console.warn(`Path "${s}" used for ${i} does not exist in all of the items, ${i} is disabled.`),t=!1)}),t}function xe(r){return[void 0,null].indexOf(r)>=0?"":isNaN(r)?r.toString():r}function dt(r,i){return r=xe(r),i=xe(i),r<i?-1:r>i?1:0}function Ts(r,i){return r.sort((e,t)=>i.map(s=>s.direction==="asc"?dt(se(s.path,e),se(s.path,t)):s.direction==="desc"?dt(se(s.path,t),se(s.path,e)):0).reduce((s,n)=>s!==0?s:n,0))}function zs(r,i){return r.filter(e=>i.every(t=>{const s=xe(se(t.path,e)),n=xe(t.value).toString().toLowerCase();return s.toString().toLowerCase().includes(n)}))}const Ps=r=>(i,e)=>{let t=r?[...r]:[];i.filters&&lt(i.filters,"filtering",t)&&(t=zs(t,i.filters)),Array.isArray(i.sortOrders)&&i.sortOrders.length&&lt(i.sortOrders,"sorting",t)&&(t=Ts(t,i.sortOrders));const s=Math.min(t.length,i.pageSize),n=i.page*s,o=n+s,a=t.slice(n,o);e(a,t.length)};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Rs=r=>class extends r{static get properties(){return{items:{type:Array,sync:!0}}}static get observers(){return["__dataProviderOrItemsChanged(dataProvider, items, isAttached, _filters, _sorters, items.*)"]}__setArrayDataProvider(e){const t=Ps(this.items);t.__items=e,this._arrayDataProvider=t,this.size=e.length,this.dataProvider=t}__dataProviderOrItemsChanged(e,t,s){s&&(this._arrayDataProvider?e!==this._arrayDataProvider?(this._arrayDataProvider=void 0,this.items=void 0):t?this._arrayDataProvider.__items===t?(this.clearCache(),this.size=this._flatSize):this.__setArrayDataProvider(t):(this._arrayDataProvider=void 0,this.dataProvider=void 0,this.size=0,this.clearCache()):t&&this.__setArrayDataProvider(t))}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Os=r=>class extends r{static get properties(){return{columnReorderingAllowed:{type:Boolean,value:!1},_orderBaseScope:{type:Number,value:1e7}}}static get observers(){return["_updateOrders(_columnTree)"]}ready(){super.ready(),me(this,"track",this._onTrackEvent),this._reorderGhost=this.shadowRoot.querySelector('[part="reorder-ghost"]'),this.addEventListener("touchstart",this._onTouchStart.bind(this)),this.addEventListener("touchmove",this._onTouchMove.bind(this)),this.addEventListener("touchend",this._onTouchEnd.bind(this)),this.addEventListener("contextmenu",this._onContextMenu.bind(this))}_onContextMenu(e){this.hasAttribute("reordering")&&(e.preventDefault(),Ee||this._onTrackEnd())}_onTouchStart(e){this._startTouchReorderTimeout=setTimeout(()=>{this._onTrackStart({detail:{x:e.touches[0].clientX,y:e.touches[0].clientY}})},100)}_onTouchMove(e){this._draggedColumn&&e.preventDefault(),clearTimeout(this._startTouchReorderTimeout)}_onTouchEnd(){clearTimeout(this._startTouchReorderTimeout),this._onTrackEnd()}_onTrackEvent(e){if(e.detail.state==="start"){const t=e.composedPath(),s=t[t.indexOf(this.$.header)-2];if(!s||!s._content||s._content.contains(this.getRootNode().activeElement)||this.$.scroller.hasAttribute("column-resizing"))return;this._touchDevice||this._onTrackStart(e)}else e.detail.state==="track"?this._onTrack(e):e.detail.state==="end"&&this._onTrackEnd(e)}_onTrackStart(e){if(!this.columnReorderingAllowed)return;const t=e.composedPath&&e.composedPath();if(t&&t.some(n=>n.hasAttribute&&n.hasAttribute("draggable")))return;const s=this._cellFromPoint(e.detail.x,e.detail.y);if(!(!s||!s.getAttribute("part").includes("header-cell"))){for(this.toggleAttribute("reordering",!0),this._draggedColumn=s._column;this._draggedColumn.parentElement.childElementCount===1;)this._draggedColumn=this._draggedColumn.parentElement;this._setSiblingsReorderStatus(this._draggedColumn,"allowed"),this._draggedColumn._reorderStatus="dragging",this._updateGhost(s),this._reorderGhost.style.visibility="visible",this._updateGhostPosition(e.detail.x,this._touchDevice?e.detail.y-50:e.detail.y),this._autoScroller()}}_onTrack(e){if(!this._draggedColumn)return;const t=this._cellFromPoint(e.detail.x,e.detail.y);if(!t)return;const s=this._getTargetColumn(t,this._draggedColumn);if(this._isSwapAllowed(this._draggedColumn,s)&&this._isSwappableByPosition(s,e.detail.x)){const n=this._columnTree.findIndex(f=>f.includes(s)),o=this._getColumnsInOrder(n),a=o.indexOf(this._draggedColumn),d=o.indexOf(s),_=a<d?1:-1;for(let f=a;f!==d;f+=_)this._swapColumnOrders(this._draggedColumn,o[f+_])}this._updateGhostPosition(e.detail.x,this._touchDevice?e.detail.y-50:e.detail.y),this._lastDragClientX=e.detail.x}_onTrackEnd(){this._draggedColumn&&(this.toggleAttribute("reordering",!1),this._draggedColumn._reorderStatus="",this._setSiblingsReorderStatus(this._draggedColumn,""),this._draggedColumn=null,this._lastDragClientX=null,this._reorderGhost.style.visibility="hidden",this.dispatchEvent(new CustomEvent("column-reorder",{detail:{columns:this._getColumnsInOrder()}})))}_getColumnsInOrder(e=this._columnTree.length-1){return this._columnTree[e].filter(t=>!t.hidden).sort((t,s)=>t._order-s._order)}_cellFromPoint(e=0,t=0){this._draggedColumn||this.$.scroller.toggleAttribute("no-content-pointer-events",!0);const s=this.shadowRoot.elementFromPoint(e,t);if(this.$.scroller.toggleAttribute("no-content-pointer-events",!1),s&&s._column)return s}_updateGhostPosition(e,t){const s=this._reorderGhost.getBoundingClientRect(),n=e-s.width/2,o=t-s.height/2,a=parseInt(this._reorderGhost._left||0),d=parseInt(this._reorderGhost._top||0);this._reorderGhost._left=a-(s.left-n),this._reorderGhost._top=d-(s.top-o),this._reorderGhost.style.transform=`translate(${this._reorderGhost._left}px, ${this._reorderGhost._top}px)`}_updateGhost(e){const t=this._reorderGhost;t.textContent=e._content.innerText;const s=window.getComputedStyle(e);return["boxSizing","display","width","height","background","alignItems","padding","border","flex-direction","overflow"].forEach(n=>{t.style[n]=s[n]}),t}_updateOrders(e){e!==void 0&&(e[0].forEach(t=>{t._order=0}),Dt(e[0],this._orderBaseScope,0))}_setSiblingsReorderStatus(e,t){O(e.parentNode,s=>{/column/u.test(s.localName)&&this._isSwapAllowed(s,e)&&(s._reorderStatus=t)})}_autoScroller(){if(this._lastDragClientX){const e=this._lastDragClientX-this.getBoundingClientRect().right+50,t=this.getBoundingClientRect().left-this._lastDragClientX+50;e>0?this.$.table.scrollLeft+=e/10:t>0&&(this.$.table.scrollLeft-=t/10)}this._draggedColumn&&setTimeout(()=>this._autoScroller(),10)}_isSwapAllowed(e,t){if(e&&t){const s=e!==t,n=e.parentElement===t.parentElement,o=e.frozen&&t.frozen||e.frozenToEnd&&t.frozenToEnd||!e.frozen&&!e.frozenToEnd&&!t.frozen&&!t.frozenToEnd;return s&&n&&o}}_isSwappableByPosition(e,t){const s=Array.from(this.$.header.querySelectorAll('tr:not([hidden]) [part~="cell"]')).find(a=>e.contains(a._column)),n=this.$.header.querySelector("tr:not([hidden]) [reorder-status=dragging]").getBoundingClientRect(),o=s.getBoundingClientRect();return o.left>n.left?t>o.right-n.width:t<o.left+n.width}_swapColumnOrders(e,t){[e._order,t._order]=[t._order,e._order],this._debounceUpdateFrozenColumn(),this._updateFirstAndLastColumn()}_getTargetColumn(e,t){if(e&&t){let s=e._column;for(;s.parentElement!==t.parentElement&&s!==this;)s=s.parentElement;return s.parentElement===t.parentElement?s:e._column}}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ks=r=>class extends r{ready(){super.ready();const e=this.$.scroller;me(e,"track",this._onHeaderTrack.bind(this)),e.addEventListener("touchmove",t=>e.hasAttribute("column-resizing")&&t.preventDefault()),e.addEventListener("contextmenu",t=>t.target.getAttribute("part")==="resize-handle"&&t.preventDefault()),e.addEventListener("mousedown",t=>t.target.getAttribute("part")==="resize-handle"&&t.preventDefault())}_onHeaderTrack(e){const t=e.target;if(t.getAttribute("part")==="resize-handle"){let n=t.parentElement._column;for(this.$.scroller.toggleAttribute("column-resizing",!0);n.localName==="vaadin-grid-column-group";)n=n._childColumns.slice(0).sort((p,g)=>p._order-g._order).filter(p=>!p.hidden).pop();const o=this.__isRTL,a=e.detail.x,d=Array.from(this.$.header.querySelectorAll('[part~="row"]:last-child [part~="cell"]')),_=d.find(p=>p._column===n);if(_.offsetWidth){const p=getComputedStyle(_._content),g=10+parseInt(p.paddingLeft)+parseInt(p.paddingRight)+parseInt(p.borderLeftWidth)+parseInt(p.borderRightWidth)+parseInt(p.marginLeft)+parseInt(p.marginRight);let w;const I=_.offsetWidth,T=_.getBoundingClientRect();_.hasAttribute("frozen-to-end")?w=I+(o?a-T.right:T.left-a):w=I+(o?T.left-a:a-T.right),n.width=`${Math.max(g,w)}px`,n.flexGrow=0}d.sort((p,g)=>p._column._order-g._column._order).forEach((p,g,w)=>{g<w.indexOf(_)&&(p._column.width=`${p.offsetWidth}px`,p._column.flexGrow=0)});const f=this._frozenToEndCells[0];if(f&&this.$.table.scrollWidth>this.$.table.offsetWidth){const p=f.getBoundingClientRect(),g=a-(o?p.right:p.left);(o&&g<=0||!o&&g>=0)&&(this.$.table.scrollLeft+=g)}e.detail.state==="end"&&(this.$.scroller.toggleAttribute("column-resizing",!1),this.dispatchEvent(new CustomEvent("column-resize",{detail:{resizedColumn:n}}))),this._resizeHandler()}}};/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function we(r,i,e=0){let t=i;for(const s of r.subCaches){const n=s.parentCacheIndex;if(t<=n)break;if(t<=n+s.flatSize)return we(s,t-n-1,e+1);t-=s.flatSize}return{cache:r,item:r.items[t],index:t,page:Math.floor(t/r.pageSize),level:e}}function Nt({getItemId:r},i,e,t=0,s=0){for(let n=0;n<i.items.length;n++){const o=i.items[n];if(o&&r(o)===r(e))return{cache:i,level:t,item:o,index:n,page:Math.floor(n/i.pageSize),subCache:i.getSubCache(n),flatIndex:s+i.getFlatIndex(n)}}for(const n of i.subCaches){const o=s+i.getFlatIndex(n.parentCacheIndex),a=Nt({getItemId:r},n,e,t+1,o+1);if(a)return a}}function Bt(r,[i,...e],t=0){i===1/0&&(i=r.size-1);const s=r.getFlatIndex(i),n=r.getSubCache(i);return n&&n.flatSize>0&&e.length?Bt(n,e,t+s+1):t+s}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class We{constructor(i,e,t,s,n){z(this,"context");z(this,"size",0);z(this,"pageSize");z(this,"items",[]);z(this,"pendingRequests",{});z(this,"__subCacheByIndex",{});z(this,"__flatSize",0);this.context=i,this.pageSize=e,this.size=t||0,this.parentCache=s,this.parentCacheIndex=n,this.__flatSize=t||0}get parentItem(){return this.parentCache&&this.parentCache.items[this.parentCacheIndex]}get subCaches(){return Object.values(this.__subCacheByIndex)}get isLoading(){return Object.keys(this.pendingRequests).length>0?!0:this.subCaches.some(i=>i.isLoading)}get flatSize(){return this.__flatSize}get effectiveSize(){return console.warn("<vaadin-grid> The `effectiveSize` property of ItemCache is deprecated and will be removed in Vaadin 25."),this.flatSize}recalculateFlatSize(){this.__flatSize=!this.parentItem||this.context.isExpanded(this.parentItem)?this.size+this.subCaches.reduce((i,e)=>(e.recalculateFlatSize(),i+e.flatSize),0):0}setPage(i,e){const t=i*this.pageSize;e.forEach((s,n)=>{this.items[t+n]=s})}getSubCache(i){return this.__subCacheByIndex[i]}removeSubCache(i){delete this.__subCacheByIndex[i]}removeSubCaches(){this.__subCacheByIndex={}}createSubCache(i){const e=new We(this.context,this.pageSize,0,this,i);return this.__subCacheByIndex[i]=e,e}getFlatIndex(i){const e=Math.max(0,Math.min(this.size-1,i));return this.subCaches.reduce((t,s)=>{const n=s.parentCacheIndex;return e>n?t+s.flatSize:t},e)}getItemForIndex(i){console.warn("<vaadin-grid> The `getItemForIndex` method of ItemCache is deprecated and will be removed in Vaadin 25.");const{item:e}=we(this,i);return e}getCacheAndIndex(i){console.warn("<vaadin-grid> The `getCacheAndIndex` method of ItemCache is deprecated and will be removed in Vaadin 25.");const{cache:e,index:t}=we(this,i);return{cache:e,scaledIndex:t}}updateSize(){console.warn("<vaadin-grid> The `updateSize` method of ItemCache is deprecated and will be removed in Vaadin 25."),this.recalculateFlatSize()}ensureSubCacheForScaledIndex(i){if(console.warn("<vaadin-grid> The `ensureSubCacheForScaledIndex` method of ItemCache is deprecated and will be removed in Vaadin 25."),!this.getSubCache(i)){const e=this.createSubCache(i);this.context.__controller.__loadCachePage(e,0)}}get grid(){return console.warn("<vaadin-grid> The `grid` property of ItemCache is deprecated and will be removed in Vaadin 25."),this.context.__controller.host}get itemCaches(){return console.warn("<vaadin-grid> The `itemCaches` property of ItemCache is deprecated and will be removed in Vaadin 25."),this.__subCacheByIndex}}/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Fs extends EventTarget{constructor(e,{size:t,pageSize:s,isExpanded:n,getItemId:o,dataProvider:a,dataProviderParams:d}){super();z(this,"host");z(this,"dataProvider");z(this,"dataProviderParams");z(this,"size");z(this,"pageSize");z(this,"isExpanded");z(this,"getItemId");z(this,"rootCache");this.host=e,this.pageSize=s,this.getItemId=o,this.isExpanded=n,this.dataProvider=a,this.dataProviderParams=d,this.rootCache=this.__createRootCache(t)}get flatSize(){return this.rootCache.flatSize}get __cacheContext(){return{isExpanded:this.isExpanded,__controller:this}}isLoading(){return this.rootCache.isLoading}setPageSize(e){this.pageSize=e,this.clearCache()}setDataProvider(e){this.dataProvider=e,this.clearCache()}recalculateFlatSize(){this.rootCache.recalculateFlatSize()}clearCache(){this.rootCache=this.__createRootCache(this.rootCache.size)}getFlatIndexContext(e){return we(this.rootCache,e)}getItemContext(e){return Nt({getItemId:this.getItemId},this.rootCache,e)}getFlatIndexByPath(e){return Bt(this.rootCache,e)}ensureFlatIndexLoaded(e){const{cache:t,page:s,item:n}=this.getFlatIndexContext(e);n||this.__loadCachePage(t,s)}ensureFlatIndexHierarchy(e){const{cache:t,item:s,index:n}=this.getFlatIndexContext(e);if(s&&this.isExpanded(s)&&!t.getSubCache(n)){const o=t.createSubCache(n);this.__loadCachePage(o,0)}}loadFirstPage(){this.__loadCachePage(this.rootCache,0)}__createRootCache(e){return new We(this.__cacheContext,this.pageSize,e)}__loadCachePage(e,t){if(!this.dataProvider||e.pendingRequests[t])return;let s={page:t,pageSize:this.pageSize,parentItem:e.parentItem};this.dataProviderParams&&(s={...s,...this.dataProviderParams()});const n=(o,a)=>{a!==void 0?e.size=a:s.parentItem&&(e.size=o.length),e.setPage(t,o),this.recalculateFlatSize(),this.dispatchEvent(new CustomEvent("page-received")),delete e.pendingRequests[t],this.dispatchEvent(new CustomEvent("page-loaded"))};e.pendingRequests[t]=n,this.dispatchEvent(new CustomEvent("page-requested")),this.dataProvider(s,n)}}/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ds=r=>class extends r{static get properties(){return{size:{type:Number,notify:!0,sync:!0},_flatSize:{type:Number,sync:!0},pageSize:{type:Number,value:50,observer:"_pageSizeChanged",sync:!0},dataProvider:{type:Object,notify:!0,observer:"_dataProviderChanged",sync:!0},loading:{type:Boolean,notify:!0,readOnly:!0,reflectToAttribute:!0},_hasData:{type:Boolean,value:!1,sync:!0},itemHasChildrenPath:{type:String,value:"children",observer:"__itemHasChildrenPathChanged",sync:!0},itemIdPath:{type:String,value:null,sync:!0},expandedItems:{type:Object,notify:!0,value:()=>[],sync:!0},__expandedKeys:{type:Object,computed:"__computeExpandedKeys(itemIdPath, expandedItems)"}}}static get observers(){return["_sizeChanged(size)","_expandedItemsChanged(expandedItems)"]}constructor(){super(),this._dataProviderController=new Fs(this,{size:this.size,pageSize:this.pageSize,getItemId:this.getItemId.bind(this),isExpanded:this._isExpanded.bind(this),dataProvider:this.dataProvider?this.dataProvider.bind(this):null,dataProviderParams:()=>({sortOrders:this._mapSorters(),filters:this._mapFilters()})}),this._dataProviderController.addEventListener("page-requested",this._onDataProviderPageRequested.bind(this)),this._dataProviderController.addEventListener("page-received",this._onDataProviderPageReceived.bind(this)),this._dataProviderController.addEventListener("page-loaded",this._onDataProviderPageLoaded.bind(this))}get _cache(){return console.warn("<vaadin-grid> The `_cache` property is deprecated and will be removed in Vaadin 25."),this._dataProviderController.rootCache}get _effectiveSize(){return console.warn("<vaadin-grid> The `_effectiveSize` property is deprecated and will be removed in Vaadin 25."),this._flatSize}_sizeChanged(e){this._dataProviderController.rootCache.size=e,this._dataProviderController.recalculateFlatSize(),this._flatSize=this._dataProviderController.flatSize}__itemHasChildrenPathChanged(e,t){!t&&e==="children"||this.requestContentUpdate()}_getItem(e,t){if(e>=this._flatSize)return;t.index=e;const{item:s}=this._dataProviderController.getFlatIndexContext(e);s?(this.__updateLoading(t,!1),this._updateItem(t,s),this._isExpanded(s)&&this._dataProviderController.ensureFlatIndexHierarchy(e)):(this.__updateLoading(t,!0),this._dataProviderController.ensureFlatIndexLoaded(e))}__updateLoading(e,t){const s=ne(e);Se(e,"loading",t),U(s,"loading-row-cell",t)}getItemId(e){return this.itemIdPath?Ve(this.itemIdPath,e):e}_isExpanded(e){return this.__expandedKeys&&this.__expandedKeys.has(this.getItemId(e))}_expandedItemsChanged(){this._dataProviderController.recalculateFlatSize(),this._flatSize=this._dataProviderController.flatSize,this.__updateVisibleRows()}__computeExpandedKeys(e,t){const s=t||[],n=new Set;return s.forEach(o=>{n.add(this.getItemId(o))}),n}expandItem(e){this._isExpanded(e)||(this.expandedItems=[...this.expandedItems,e])}collapseItem(e){this._isExpanded(e)&&(this.expandedItems=this.expandedItems.filter(t=>!this._itemsEqual(t,e)))}_getIndexLevel(e=0){const{level:t}=this._dataProviderController.getFlatIndexContext(e);return t}_loadPage(e,t){console.warn("<vaadin-grid> The `_loadPage` method is deprecated and will be removed in Vaadin 25."),this._dataProviderController.__loadCachePage(t,e)}_onDataProviderPageRequested(){this._setLoading(!0)}_onDataProviderPageReceived(){this._flatSize=this._dataProviderController.flatSize,this._getRenderedRows().forEach(e=>{this._dataProviderController.ensureFlatIndexHierarchy(e.index)}),this._hasData=!0}_onDataProviderPageLoaded(){this._debouncerApplyCachedData=x.debounce(this._debouncerApplyCachedData,B.after(0),()=>{this._setLoading(!1),this._getRenderedRows().forEach(e=>{const{item:t}=this._dataProviderController.getFlatIndexContext(e.index);t&&this._getItem(e.index,e)}),this.__scrollToPendingIndexes(),this.__dispatchPendingBodyCellFocus()}),this._dataProviderController.isLoading()||this._debouncerApplyCachedData.flush()}__debounceClearCache(){this.__clearCacheDebouncer=x.debounce(this.__clearCacheDebouncer,K,()=>this.clearCache())}clearCache(){this._dataProviderController.clearCache(),this._dataProviderController.rootCache.size=this.size,this._dataProviderController.recalculateFlatSize(),this._hasData=!1,this.__updateVisibleRows(),this.__virtualizer.size||this._dataProviderController.loadFirstPage()}_pageSizeChanged(e,t){this._dataProviderController.setPageSize(e),t!==void 0&&e!==t&&this.clearCache()}_checkSize(){this.size===void 0&&this._flatSize===0&&console.warn("The <vaadin-grid> needs the total number of items in order to display rows, which you can specify either by setting the `size` property, or by providing it to the second argument of the `dataProvider` function `callback` call.")}_dataProviderChanged(e,t){this._dataProviderController.setDataProvider(e?e.bind(this):null),t!==void 0&&this.clearCache(),this._ensureFirstPageLoaded(),this._debouncerCheckSize=x.debounce(this._debouncerCheckSize,B.after(2e3),this._checkSize.bind(this))}_ensureFirstPageLoaded(){this._hasData||this._dataProviderController.loadFirstPage()}_itemsEqual(e,t){return this.getItemId(e)===this.getItemId(t)}_getItemIndexInArray(e,t){let s=-1;return t.forEach((n,o)=>{this._itemsEqual(n,e)&&(s=o)}),s}scrollToIndex(...e){let t;for(;t!==(t=this._dataProviderController.getFlatIndexByPath(e));)this._scrollToFlatIndex(t);(this._dataProviderController.isLoading()||!this.clientHeight)&&(this.__pendingScrollToIndexes=e)}__scrollToPendingIndexes(){if(this.__pendingScrollToIndexes&&this.$.items.children.length){const e=this.__pendingScrollToIndexes;delete this.__pendingScrollToIndexes,this.scrollToIndex(...e)}}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const de={BETWEEN:"between",ON_TOP_OR_BETWEEN:"on-top-or-between",ON_GRID:"on-grid"},W={ON_TOP:"on-top",ABOVE:"above",BELOW:"below",EMPTY:"empty"},Ms=!("draggable"in document.createElement("div")),Ls=r=>class extends r{static get properties(){return{dropMode:{type:String,sync:!0},rowsDraggable:{type:Boolean,sync:!0},dragFilter:{type:Function,sync:!0},dropFilter:{type:Function,sync:!0},__dndAutoScrollThreshold:{value:50}}}static get observers(){return["_dragDropAccessChanged(rowsDraggable, dropMode, dragFilter, dropFilter, loading)"]}ready(){super.ready(),this.$.table.addEventListener("dragstart",this._onDragStart.bind(this)),this.$.table.addEventListener("dragend",this._onDragEnd.bind(this)),this.$.table.addEventListener("dragover",this._onDragOver.bind(this)),this.$.table.addEventListener("dragleave",this._onDragLeave.bind(this)),this.$.table.addEventListener("drop",this._onDrop.bind(this)),this.$.table.addEventListener("dragenter",e=>{this.dropMode&&(e.preventDefault(),e.stopPropagation())})}_onDragStart(e){if(this.rowsDraggable){let t=e.target;if(t.localName==="vaadin-grid-cell-content"&&(t=t.assignedSlot.parentNode.parentNode),t.parentNode!==this.$.items)return;if(e.stopPropagation(),this.toggleAttribute("dragging-rows",!0),this._safari){const a=t.style.transform;t.style.top=/translateY\((.*)\)/u.exec(a)[1],t.style.transform="none",requestAnimationFrame(()=>{t.style.top="",t.style.transform=a})}const s=t.getBoundingClientRect();Ms?e.dataTransfer.setDragImage(t):e.dataTransfer.setDragImage(t,e.clientX-s.left,e.clientY-s.top);let n=[t];this._isSelected(t._item)&&(n=this.__getViewportRows().filter(a=>this._isSelected(a._item)).filter(a=>!this.dragFilter||this.dragFilter(this.__getRowModel(a)))),e.dataTransfer.setData("text",this.__formatDefaultTransferData(n)),_e(t,{dragstart:n.length>1?`${n.length}`:""}),this.style.setProperty("--_grid-drag-start-x",`${e.clientX-s.left+20}px`),this.style.setProperty("--_grid-drag-start-y",`${e.clientY-s.top+10}px`),requestAnimationFrame(()=>{_e(t,{dragstart:!1}),this.style.setProperty("--_grid-drag-start-x",""),this.style.setProperty("--_grid-drag-start-y","")});const o=new CustomEvent("grid-dragstart",{detail:{draggedItems:n.map(a=>a._item),setDragData:(a,d)=>e.dataTransfer.setData(a,d),setDraggedItemsCount:a=>t.setAttribute("dragstart",a)}});o.originalEvent=e,this.dispatchEvent(o)}}_onDragEnd(e){this.toggleAttribute("dragging-rows",!1),e.stopPropagation();const t=new CustomEvent("grid-dragend");t.originalEvent=e,this.dispatchEvent(t)}_onDragLeave(e){e.stopPropagation(),this._clearDragStyles()}_onDragOver(e){if(this.dropMode){if(this._dropLocation=void 0,this._dragOverItem=void 0,this.__dndAutoScroll(e.clientY)){this._clearDragStyles();return}let t=e.composedPath().find(s=>s.localName==="tr");if(!this._flatSize||this.dropMode===de.ON_GRID)this._dropLocation=W.EMPTY;else if(!t||t.parentNode!==this.$.items){if(t)return;if(this.dropMode===de.BETWEEN||this.dropMode===de.ON_TOP_OR_BETWEEN)t=Array.from(this.$.items.children).filter(s=>!s.hidden).pop(),this._dropLocation=W.BELOW;else return}else{const s=t.getBoundingClientRect();if(this._dropLocation=W.ON_TOP,this.dropMode===de.BETWEEN){const n=e.clientY-s.top<s.bottom-e.clientY;this._dropLocation=n?W.ABOVE:W.BELOW}else this.dropMode===de.ON_TOP_OR_BETWEEN&&(e.clientY-s.top<s.height/3?this._dropLocation=W.ABOVE:e.clientY-s.top>s.height/3*2&&(this._dropLocation=W.BELOW))}if(t&&t.hasAttribute("drop-disabled")){this._dropLocation=void 0;return}e.stopPropagation(),e.preventDefault(),this._dropLocation===W.EMPTY?this.toggleAttribute("dragover",!0):t?(this._dragOverItem=t._item,t.getAttribute("dragover")!==this._dropLocation&&ot(t,{dragover:this._dropLocation})):this._clearDragStyles()}}__dndAutoScroll(e){if(this.__dndAutoScrolling)return!0;const t=this.$.header.getBoundingClientRect().bottom,s=this.$.footer.getBoundingClientRect().top,n=t-e+this.__dndAutoScrollThreshold,o=e-s+this.__dndAutoScrollThreshold;let a=0;if(o>0?a=o*2:n>0&&(a=-n*2),a){const d=this.$.table.scrollTop;if(this.$.table.scrollTop+=a,d!==this.$.table.scrollTop)return this.__dndAutoScrolling=!0,setTimeout(()=>{this.__dndAutoScrolling=!1},20),!0}}__getViewportRows(){const e=this.$.header.getBoundingClientRect().bottom,t=this.$.footer.getBoundingClientRect().top;return Array.from(this.$.items.children).filter(s=>{const n=s.getBoundingClientRect();return n.bottom>e&&n.top<t})}_clearDragStyles(){this.removeAttribute("dragover"),O(this.$.items,e=>{ot(e,{dragover:null})})}_onDrop(e){if(this.dropMode){e.stopPropagation(),e.preventDefault();const t=e.dataTransfer.types&&Array.from(e.dataTransfer.types).map(n=>({type:n,data:e.dataTransfer.getData(n)}));this._clearDragStyles();const s=new CustomEvent("grid-drop",{bubbles:e.bubbles,cancelable:e.cancelable,detail:{dropTargetItem:this._dragOverItem,dropLocation:this._dropLocation,dragData:t}});s.originalEvent=e,this.dispatchEvent(s)}}__formatDefaultTransferData(e){return e.map(t=>Array.from(t.children).filter(s=>!s.hidden&&s.getAttribute("part").indexOf("details-cell")===-1).sort((s,n)=>s._column._order>n._column._order?1:-1).map(s=>s._content.textContent.trim()).filter(s=>s).join("	")).join(`
`)}_dragDropAccessChanged(){this.filterDragAndDrop()}filterDragAndDrop(){O(this.$.items,e=>{e.hidden||this._filterDragAndDrop(e,this.__getRowModel(e))})}_filterDragAndDrop(e,t){const s=this.loading||e.hasAttribute("loading"),n=!this.rowsDraggable||s||this.dragFilter&&!this.dragFilter(t),o=!this.dropMode||s||this.dropFilter&&!this.dropFilter(t);oe(e,a=>{n?a._content.removeAttribute("draggable"):a._content.setAttribute("draggable",!0)}),_e(e,{"drag-disabled":!!n,"drop-disabled":!!o})}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */function Vt(r,i){if(!r||!i||r.length!==i.length)return!1;for(let e=0,t=r.length;e<t;e++)if(r[e]instanceof Array&&i[e]instanceof Array){if(!Vt(r[e],i[e]))return!1}else if(r[e]!==i[e])return!1;return!0}const $s=r=>class extends r{static get properties(){return{_columnTree:Object}}ready(){super.ready(),this._addNodeObserver()}_hasColumnGroups(e){return e.some(t=>t.localName==="vaadin-grid-column-group")}_getChildColumns(e){return q.getColumns(e)}_flattenColumnGroups(e){return e.map(t=>t.localName==="vaadin-grid-column-group"?this._getChildColumns(t):[t]).reduce((t,s)=>t.concat(s),[])}_getColumnTree(){const e=q.getColumns(this),t=[e];let s=e;for(;this._hasColumnGroups(s);)s=this._flattenColumnGroups(s),t.push(s);return t}_debounceUpdateColumnTree(){this.__updateColumnTreeDebouncer=x.debounce(this.__updateColumnTreeDebouncer,K,()=>this._updateColumnTree())}_updateColumnTree(){const e=this._getColumnTree();Vt(e,this._columnTree)||(e.forEach(t=>{t.forEach(s=>{s.performUpdate&&s.performUpdate()})}),this._columnTree=e)}_addNodeObserver(){this._observer=new q(this,(e,t)=>{const s=t.flatMap(o=>o._allCells),n=o=>s.filter(a=>a&&a._content.contains(o)).length;this.__removeSorters(this._sorters.filter(n)),this.__removeFilters(this._filters.filter(n)),this._debounceUpdateColumnTree(),this._debouncerCheckImports=x.debounce(this._debouncerCheckImports,B.after(2e3),this._checkImports.bind(this)),this._ensureFirstPageLoaded()})}_checkImports(){["vaadin-grid-column-group","vaadin-grid-filter","vaadin-grid-filter-column","vaadin-grid-tree-toggle","vaadin-grid-selection-column","vaadin-grid-sort-column","vaadin-grid-sorter"].forEach(e=>{this.querySelector(e)&&!customElements.get(e)&&console.warn(`Make sure you have imported the required module for <${e}> element.`)})}_updateFirstAndLastColumn(){Array.from(this.shadowRoot.querySelectorAll("tr")).forEach(e=>this._updateFirstAndLastColumnForRow(e))}_updateFirstAndLastColumnForRow(e){Array.from(e.querySelectorAll('[part~="cell"]:not([part~="details-cell"])')).sort((t,s)=>t._column._order-s._column._order).forEach((t,s,n)=>{ee(t,"first-column",s===0),ee(t,"last-column",s===n.length-1)})}_isColumnElement(e){return e.nodeType===Node.ELEMENT_NODE&&/\bcolumn\b/u.test(e.localName)}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Hs=r=>class extends r{getEventContext(e){const t={},s=e.__composedPath||e.composedPath(),n=s[s.indexOf(this.$.table)-3];return n&&(t.section=["body","header","footer","details"].find(o=>n.getAttribute("part").indexOf(o)>-1),n._column&&(t.column=n._column),(t.section==="body"||t.section==="details")&&Object.assign(t,this.__getRowModel(n.parentElement))),t}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ns=r=>class extends r{static get properties(){return{_filters:{type:Array,value:()=>[]}}}constructor(){super(),this._filterChanged=this._filterChanged.bind(this),this.addEventListener("filter-changed",this._filterChanged)}_filterChanged(e){e.stopPropagation(),this.__addFilter(e.target),this.__applyFilters()}__removeFilters(e){e.length!==0&&(this._filters=this._filters.filter(t=>e.indexOf(t)<0),this.__applyFilters())}__addFilter(e){this._filters.indexOf(e)===-1&&this._filters.push(e)}__applyFilters(){this.dataProvider&&this.isAttached&&this.clearCache()}_mapFilters(){return this._filters.map(e=>({path:e.path,value:e.value}))}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Bs=r=>class extends r{static get properties(){return{_headerFocusable:{type:Object,observer:"_focusableChanged",sync:!0},_itemsFocusable:{type:Object,observer:"_focusableChanged",sync:!0},_footerFocusable:{type:Object,observer:"_focusableChanged",sync:!0},_navigatingIsHidden:Boolean,_focusedItemIndex:{type:Number,value:0},_focusedColumnOrder:Number,_focusedCell:{type:Object,observer:"_focusedCellChanged",sync:!0},interacting:{type:Boolean,value:!1,reflectToAttribute:!0,readOnly:!0,observer:"_interactingChanged"}}}get __rowFocusMode(){return this.__isRow(this._itemsFocusable)||this.__isRow(this._headerFocusable)||this.__isRow(this._footerFocusable)}set __rowFocusMode(e){["_itemsFocusable","_footerFocusable","_headerFocusable"].forEach(t=>{const s=this[t];if(e){const n=s&&s.parentElement;this.__isCell(s)?this[t]=n:this.__isCell(n)&&(this[t]=n.parentElement)}else if(!e&&this.__isRow(s)){const n=s.firstElementChild;this[t]=n._focusButton||n}})}ready(){super.ready(),!(this._ios||this._android)&&(this.addEventListener("keydown",this._onKeyDown),this.addEventListener("keyup",this._onKeyUp),this.addEventListener("focusin",this._onFocusIn),this.addEventListener("focusout",this._onFocusOut),this.$.table.addEventListener("focusin",this._onContentFocusIn.bind(this)),this.addEventListener("mousedown",()=>{this.toggleAttribute("navigating",!1),this._isMousedown=!0,this._focusedColumnOrder=void 0}),this.addEventListener("mouseup",()=>{this._isMousedown=!1}))}_focusableChanged(e,t){t&&t.setAttribute("tabindex","-1"),e&&this._updateGridSectionFocusTarget(e)}_focusedCellChanged(e,t){t&&bt(t,"part","focused-cell"),e&&gt(e,"part","focused-cell")}_interactingChanged(){this._updateGridSectionFocusTarget(this._headerFocusable),this._updateGridSectionFocusTarget(this._itemsFocusable),this._updateGridSectionFocusTarget(this._footerFocusable)}__updateItemsFocusable(){if(!this._itemsFocusable)return;const e=this.shadowRoot.activeElement===this._itemsFocusable;this._getRenderedRows().forEach(t=>{if(t.index===this._focusedItemIndex)if(this.__rowFocusMode)this._itemsFocusable=t;else{let s=this._itemsFocusable.parentElement,n=this._itemsFocusable;if(s){this.__isCell(s)&&(n=s,s=s.parentElement);const o=[...s.children].indexOf(n);this._itemsFocusable=this.__getFocusable(t,t.children[o])}}}),e&&this._itemsFocusable.focus()}_onKeyDown(e){const t=e.key;let s;switch(t){case"ArrowUp":case"ArrowDown":case"ArrowLeft":case"ArrowRight":case"PageUp":case"PageDown":case"Home":case"End":s="Navigation";break;case"Enter":case"Escape":case"F2":s="Interaction";break;case"Tab":s="Tab";break;case" ":s="Space";break}this._detectInteracting(e),this.interacting&&s!=="Interaction"&&(s=void 0),s&&this[`_on${s}KeyDown`](e,t)}_ensureScrolledToIndex(e){[...this.$.items.children].find(s=>s.index===e)?this.__scrollIntoViewport(e):this.scrollToIndex(e)}__isRowExpandable(e){if(this.itemHasChildrenPath){const t=e._item;return!!(t&&Ve(this.itemHasChildrenPath,t)&&!this._isExpanded(t))}}__isRowCollapsible(e){return this._isExpanded(e._item)}__isDetailsCell(e){return e.matches('[part~="details-cell"]')}__isCell(e){return e instanceof HTMLTableCellElement}__isRow(e){return e instanceof HTMLTableRowElement}__getIndexOfChildElement(e){return Array.prototype.indexOf.call(e.parentNode.children,e)}_onNavigationKeyDown(e,t){e.preventDefault();const s=this._lastVisibleIndex-this._firstVisibleIndex-1,n=this.__isRTL;let o=0,a=0;switch(t){case"ArrowRight":o=n?-1:1;break;case"ArrowLeft":o=n?1:-1;break;case"Home":this.__rowFocusMode||e.ctrlKey?a=-1/0:o=-1/0;break;case"End":this.__rowFocusMode||e.ctrlKey?a=1/0:o=1/0;break;case"ArrowDown":a=1;break;case"ArrowUp":a=-1;break;case"PageDown":a=s;break;case"PageUp":a=-s;break}const d=e.composedPath().find(g=>this.__isRow(g)),_=e.composedPath().find(g=>this.__isCell(g));if(this.__rowFocusMode&&!d||!this.__rowFocusMode&&!_)return;const f=n?"ArrowLeft":"ArrowRight",p=n?"ArrowRight":"ArrowLeft";if(t===f){if(this.__rowFocusMode){if(this.__isRowExpandable(d)){this.expandItem(d._item);return}this.__rowFocusMode=!1,this._onCellNavigation(d.firstElementChild,0,0);return}}else if(t===p)if(this.__rowFocusMode){if(this.__isRowCollapsible(d)){this.collapseItem(d._item);return}}else{const g=[...d.children].sort((w,I)=>w._order-I._order);if(_===g[0]||this.__isDetailsCell(_)){this.__rowFocusMode=!0,this._onRowNavigation(d,0);return}}this.__rowFocusMode?this._onRowNavigation(d,a):this._onCellNavigation(_,o,a)}_onRowNavigation(e,t){const{dstRow:s}=this.__navigateRows(t,e);s&&s.focus()}__getIndexInGroup(e,t){return e.parentNode===this.$.items?t!==void 0?t:e.index:this.__getIndexOfChildElement(e)}__navigateRows(e,t,s){const n=this.__getIndexInGroup(t,this._focusedItemIndex),o=t.parentNode,a=(o===this.$.items?this._flatSize:o.children.length)-1;let d=Math.max(0,Math.min(n+e,a));if(o!==this.$.items){if(d>n)for(;d<a&&o.children[d].hidden;)d+=1;else if(d<n)for(;d>0&&o.children[d].hidden;)d-=1;return this.toggleAttribute("navigating",!0),{dstRow:o.children[d]}}let _=!1;if(s){const f=this.__isDetailsCell(s);if(o===this.$.items){const p=t._item,{item:g}=this._dataProviderController.getFlatIndexContext(d);f?_=e===0:_=e===1&&this._isDetailsOpened(p)||e===-1&&d!==n&&this._isDetailsOpened(g),_!==f&&(e===1&&_||e===-1&&!_)&&(d=n)}}return this._ensureScrolledToIndex(d),this._focusedItemIndex=d,this.toggleAttribute("navigating",!0),{dstRow:[...o.children].find(f=>!f.hidden&&f.index===d),dstIsRowDetails:_}}_onCellNavigation(e,t,s){const n=e.parentNode,{dstRow:o,dstIsRowDetails:a}=this.__navigateRows(s,n,e);if(!o)return;let d=this.__getIndexOfChildElement(e);this.$.items.contains(e)&&(d=[...this.$.sizer.children].findIndex(g=>g._column===e._column));const _=this.__isDetailsCell(e),f=n.parentNode,p=this.__getIndexInGroup(n,this._focusedItemIndex);if(this._focusedColumnOrder===void 0&&(_?this._focusedColumnOrder=0:this._focusedColumnOrder=this._getColumns(f,p).filter(g=>!g.hidden)[d]._order),a)[...o.children].find(w=>this.__isDetailsCell(w)).focus();else{const g=this.__getIndexInGroup(o,this._focusedItemIndex),w=this._getColumns(f,g).filter(S=>!S.hidden),I=w.map(S=>S._order).sort((S,$)=>S-$),T=I.length-1,C=I.indexOf(I.slice(0).sort((S,$)=>Math.abs(S-this._focusedColumnOrder)-Math.abs($-this._focusedColumnOrder))[0]),G=s===0&&_?C:Math.max(0,Math.min(C+t,T));G!==C&&(this._focusedColumnOrder=void 0);const M=w.reduce((S,$,te)=>(S[$._order]=te,S),{})[I[G]];let H;if(this.$.items.contains(e)){const S=this.$.sizer.children[M];this._lazyColumns&&(this.__isColumnInViewport(S._column)||S.scrollIntoView(),this.__updateColumnsBodyContentHidden(),this.__updateHorizontalScrollPosition()),H=[...o.children].find($=>$._column===S._column),this._scrollHorizontallyToCell(H)}else H=o.children[M],this._scrollHorizontallyToCell(H);H.focus()}}_onInteractionKeyDown(e,t){const s=e.composedPath()[0],n=s.localName==="input"&&!/^(button|checkbox|color|file|image|radio|range|reset|submit)$/iu.test(s.type);let o;switch(t){case"Enter":o=this.interacting?!n:!0;break;case"Escape":o=!1;break;case"F2":o=!this.interacting;break}const{cell:a}=this._getGridEventLocation(e);if(this.interacting!==o&&a!==null)if(o){const d=a._content.querySelector("[focus-target]")||[...a._content.querySelectorAll("*")].find(_=>this._isFocusable(_));d&&(e.preventDefault(),d.focus(),this._setInteracting(!0),this.toggleAttribute("navigating",!1))}else e.preventDefault(),this._focusedColumnOrder=void 0,a.focus(),this._setInteracting(!1),this.toggleAttribute("navigating",!0);t==="Escape"&&this._hideTooltip(!0)}_predictFocusStepTarget(e,t){const s=[this.$.table,this._headerFocusable,this._itemsFocusable,this._footerFocusable,this.$.focusexit];let n=s.indexOf(e);for(n+=t;n>=0&&n<=s.length-1;){let a=s[n];if(a&&!this.__rowFocusMode&&(a=s[n].parentNode),!a||a.hidden)n+=t;else break}let o=s[n];if(o&&!this.__isHorizontallyInViewport(o)){const a=this._getColumnsInOrder().find(d=>this.__isColumnInViewport(d));if(a)if(o===this._headerFocusable)o=a._headerCell;else if(o===this._itemsFocusable){const d=o._column._cells.indexOf(o);o=a._cells[d]}else o===this._footerFocusable&&(o=a._footerCell)}return o}_onTabKeyDown(e){const t=this._predictFocusStepTarget(e.composedPath()[0],e.shiftKey?-1:1);if(t){if(e.stopPropagation(),t===this.$.table)this.$.table.focus();else if(t===this.$.focusexit)this.$.focusexit.focus();else if(t===this._itemsFocusable){let s=t;const n=this.__isRow(t)?t:t.parentNode;if(this._ensureScrolledToIndex(this._focusedItemIndex),n.index!==this._focusedItemIndex&&this.__isCell(t)){const o=Array.from(n.children).indexOf(this._itemsFocusable),a=Array.from(this.$.items.children).find(d=>!d.hidden&&d.index===this._focusedItemIndex);a&&(s=a.children[o])}e.preventDefault(),s.focus()}else e.preventDefault(),t.focus();this.toggleAttribute("navigating",!0)}}_onSpaceKeyDown(e){e.preventDefault();const t=e.composedPath()[0],s=this.__isRow(t);(s||!t._content||!t._content.firstElementChild)&&this.dispatchEvent(new CustomEvent(s?"row-activate":"cell-activate",{detail:{model:this.__getRowModel(s?t:t.parentElement)}}))}_onKeyUp(e){if(!/^( |SpaceBar)$/u.test(e.key)||this.interacting)return;e.preventDefault();const t=e.composedPath()[0];if(t._content&&t._content.firstElementChild){const s=this.hasAttribute("navigating");t._content.firstElementChild.dispatchEvent(new MouseEvent("click",{shiftKey:e.shiftKey,bubbles:!0,composed:!0,cancelable:!0})),this.toggleAttribute("navigating",s)}}_onFocusIn(e){this._isMousedown||this.toggleAttribute("navigating",!0);const t=e.composedPath()[0];t===this.$.table||t===this.$.focusexit?(this._predictFocusStepTarget(t,t===this.$.table?1:-1).focus(),this._setInteracting(!1)):this._detectInteracting(e)}_onFocusOut(e){this.toggleAttribute("navigating",!1),this._detectInteracting(e),this._hideTooltip(),this._focusedCell=null}_onContentFocusIn(e){const{section:t,cell:s,row:n}=this._getGridEventLocation(e);if(!(!s&&!this.__rowFocusMode)){if(this._detectInteracting(e),t&&(s||n))if(this._activeRowGroup=t,this.$.header===t?this._headerFocusable=this.__getFocusable(n,s):this.$.items===t?this._itemsFocusable=this.__getFocusable(n,s):this.$.footer===t&&(this._footerFocusable=this.__getFocusable(n,s)),s){const o=this.getEventContext(e);this.__pendingBodyCellFocus=this.loading&&o.section==="body",this.__pendingBodyCellFocus||s.dispatchEvent(new CustomEvent("cell-focus",{bubbles:!0,composed:!0,detail:{context:o}})),this._focusedCell=s._focusButton||s,mi()&&e.target===s&&this._showTooltip(e)}else this._focusedCell=null;this._detectFocusedItemIndex(e)}}__dispatchPendingBodyCellFocus(){this.__pendingBodyCellFocus&&this.shadowRoot.activeElement===this._itemsFocusable&&this._itemsFocusable.dispatchEvent(new Event("focusin",{bubbles:!0,composed:!0}))}__getFocusable(e,t){return this.__rowFocusMode?e:t._focusButton||t}_detectInteracting(e){const t=e.composedPath().some(s=>s.localName==="vaadin-grid-cell-content");this._setInteracting(t),this.__updateHorizontalScrollPosition()}_detectFocusedItemIndex(e){const{section:t,row:s}=this._getGridEventLocation(e);t===this.$.items&&(this._focusedItemIndex=s.index)}_updateGridSectionFocusTarget(e){if(!e)return;const t=this._getGridSectionFromFocusTarget(e),s=this.interacting&&t===this._activeRowGroup;e.tabIndex=s?-1:0}_preventScrollerRotatingCellFocus(e,t){e.index===this._focusedItemIndex&&this.hasAttribute("navigating")&&this._activeRowGroup===this.$.items&&(this._navigatingIsHidden=!0,this.toggleAttribute("navigating",!1)),t===this._focusedItemIndex&&this._navigatingIsHidden&&(this._navigatingIsHidden=!1,this.toggleAttribute("navigating",!0))}_getColumns(e,t){let s=this._columnTree.length-1;return e===this.$.header?s=t:e===this.$.footer&&(s=this._columnTree.length-1-t),this._columnTree[s]}__isValidFocusable(e){return this.$.table.contains(e)&&e.offsetHeight}_resetKeyboardNavigation(){if(!this.$&&this.performUpdate&&this.performUpdate(),["header","footer"].forEach(e=>{if(!this.__isValidFocusable(this[`_${e}Focusable`])){const t=[...this.$[e].children].find(n=>n.offsetHeight),s=t?[...t.children].find(n=>!n.hidden):null;t&&s&&(this[`_${e}Focusable`]=this.__getFocusable(t,s))}}),!this.__isValidFocusable(this._itemsFocusable)&&this.$.items.firstElementChild){const e=this.__getFirstVisibleItem(),t=e?[...e.children].find(s=>!s.hidden):null;t&&e&&(this._focusedColumnOrder=void 0,this._itemsFocusable=this.__getFocusable(e,t))}else this.__updateItemsFocusable()}_scrollHorizontallyToCell(e){if(e.hasAttribute("frozen")||e.hasAttribute("frozen-to-end")||this.__isDetailsCell(e))return;const t=e.getBoundingClientRect(),s=e.parentNode,n=Array.from(s.children).indexOf(e),o=this.$.table.getBoundingClientRect();let a=o.left,d=o.right;for(let _=n-1;_>=0;_--){const f=s.children[_];if(!(f.hasAttribute("hidden")||this.__isDetailsCell(f))&&(f.hasAttribute("frozen")||f.hasAttribute("frozen-to-end"))){a=f.getBoundingClientRect().right;break}}for(let _=n+1;_<s.children.length;_++){const f=s.children[_];if(!(f.hasAttribute("hidden")||this.__isDetailsCell(f))&&(f.hasAttribute("frozen")||f.hasAttribute("frozen-to-end"))){d=f.getBoundingClientRect().left;break}}t.left<a&&(this.$.table.scrollLeft+=Math.round(t.left-a)),t.right>d&&(this.$.table.scrollLeft+=Math.round(t.right-d))}_getGridEventLocation(e){const t=e.composedPath(),s=t.indexOf(this.$.table),n=s>=1?t[s-1]:null,o=s>=2?t[s-2]:null,a=s>=3?t[s-3]:null;return{section:n,row:o,cell:a}}_getGridSectionFromFocusTarget(e){return e===this._headerFocusable?this.$.header:e===this._itemsFocusable?this.$.items:e===this._footerFocusable?this.$.footer:null}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Vs=r=>class extends r{static get properties(){return{detailsOpenedItems:{type:Array,value:()=>[],sync:!0},rowDetailsRenderer:{type:Function,sync:!0},_detailsCells:{type:Array}}}static get observers(){return["_detailsOpenedItemsChanged(detailsOpenedItems, rowDetailsRenderer)","_rowDetailsRendererChanged(rowDetailsRenderer)"]}ready(){super.ready(),this._detailsCellResizeObserver=new ResizeObserver(e=>{e.forEach(({target:t})=>{this._updateDetailsCellHeight(t.parentElement)}),this.__virtualizer.__adapter._resizeHandler()})}_rowDetailsRendererChanged(e){e&&this._columnTree&&O(this.$.items,t=>{if(!t.querySelector("[part~=details-cell]")){this._updateRow(t,this._columnTree[this._columnTree.length-1]);const s=this._isDetailsOpened(t._item);this._toggleDetailsCell(t,s)}})}_detailsOpenedItemsChanged(e,t){O(this.$.items,s=>{if(s.hasAttribute("details-opened")){this._updateItem(s,s._item);return}t&&this._isDetailsOpened(s._item)&&this._updateItem(s,s._item)})}_configureDetailsCell(e){e.setAttribute("part","cell details-cell"),e.toggleAttribute("frozen",!0),this._detailsCellResizeObserver.observe(e)}_toggleDetailsCell(e,t){const s=e.querySelector('[part~="details-cell"]');s&&(s.hidden=!t,!s.hidden&&this.rowDetailsRenderer&&(s._renderer=this.rowDetailsRenderer))}_updateDetailsCellHeight(e){const t=e.querySelector('[part~="details-cell"]');t&&(this.__updateDetailsRowPadding(e,t),requestAnimationFrame(()=>this.__updateDetailsRowPadding(e,t)))}__updateDetailsRowPadding(e,t){t.hidden?e.style.removeProperty("padding-bottom"):e.style.setProperty("padding-bottom",`${t.offsetHeight}px`)}_updateDetailsCellHeights(){O(this.$.items,e=>{this._updateDetailsCellHeight(e)})}_isDetailsOpened(e){return this.detailsOpenedItems&&this._getItemIndexInArray(e,this.detailsOpenedItems)!==-1}openItemDetails(e){this._isDetailsOpened(e)||(this.detailsOpenedItems=[...this.detailsOpenedItems,e])}closeItemDetails(e){this._isDetailsOpened(e)&&(this.detailsOpenedItems=this.detailsOpenedItems.filter(t=>!this._itemsEqual(t,e)))}};/**
 * @license
 * Copyright (c) 2022 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Te=document.createElement("div");Te.style.position="fixed";Te.style.clip="rect(0px, 0px, 0px, 0px)";Te.setAttribute("aria-live","polite");document.body.appendChild(Te);/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const ht={SCROLLING:500,UPDATE_CONTENT_VISIBILITY:100},Gs=r=>class extends ds(r){static get properties(){return{columnRendering:{type:String,value:"eager",sync:!0},_frozenCells:{type:Array,value:()=>[]},_frozenToEndCells:{type:Array,value:()=>[]},_rowWithFocusedElement:Element}}static get observers(){return["__columnRenderingChanged(_columnTree, columnRendering)"]}get _scrollLeft(){return this.$.table.scrollLeft}get _scrollTop(){return this.$.table.scrollTop}set _scrollTop(e){this.$.table.scrollTop=e}get _lazyColumns(){return this.columnRendering==="lazy"}ready(){super.ready(),this.scrollTarget=this.$.table,this.$.items.addEventListener("focusin",e=>{const t=e.composedPath().indexOf(this.$.items);this._rowWithFocusedElement=e.composedPath()[t-1]}),this.$.items.addEventListener("focusout",()=>{this._rowWithFocusedElement=void 0}),this.$.table.addEventListener("scroll",()=>this._afterScroll())}_onResize(){if(this._updateOverflow(),this.__updateHorizontalScrollPosition(),this._firefox){const e=!re(this);e&&this.__previousVisible===!1&&(this._scrollTop=this.__memorizedScrollTop||0),this.__previousVisible=e}}_scrollToFlatIndex(e){e=Math.min(this._flatSize-1,Math.max(0,e)),this.__virtualizer.scrollToIndex(e),this.__scrollIntoViewport(e)}__scrollIntoViewport(e){const t=[...this.$.items.children].find(s=>s.index===e);if(t){const s=t.getBoundingClientRect(),n=this.$.footer.getBoundingClientRect().top,o=this.$.header.getBoundingClientRect().bottom;s.bottom>n?this.$.table.scrollTop+=s.bottom-n:s.top<o&&(this.$.table.scrollTop-=o-s.top)}}_scheduleScrolling(){this._scrollingFrame||(this._scrollingFrame=requestAnimationFrame(()=>this.$.scroller.toggleAttribute("scrolling",!0))),this._debounceScrolling=x.debounce(this._debounceScrolling,B.after(ht.SCROLLING),()=>{cancelAnimationFrame(this._scrollingFrame),delete this._scrollingFrame,this.$.scroller.toggleAttribute("scrolling",!1)})}_afterScroll(){this.__updateHorizontalScrollPosition(),this.hasAttribute("reordering")||this._scheduleScrolling(),this.hasAttribute("navigating")||this._hideTooltip(!0),this._updateOverflow(),this._debounceColumnContentVisibility=x.debounce(this._debounceColumnContentVisibility,B.after(ht.UPDATE_CONTENT_VISIBILITY),()=>{this._lazyColumns&&this.__cachedScrollLeft!==this._scrollLeft&&(this.__cachedScrollLeft=this._scrollLeft,this.__updateColumnsBodyContentHidden())}),this._firefox&&!re(this)&&this.__previousVisible!==!1&&(this.__memorizedScrollTop=this._scrollTop)}__updateColumnsBodyContentHidden(){if(!this._columnTree)return;const e=this._getColumnsInOrder();if(!e[0]||!e[0]._sizerCell)return;let t=!1;if(e.forEach(s=>{const n=this._lazyColumns&&!this.__isColumnInViewport(s);s._bodyContentHidden!==n&&(t=!0,s._cells.forEach(o=>{if(o!==s._sizerCell){if(n)o.remove();else if(o.__parentRow){const a=[...o.__parentRow.children].find(d=>e.indexOf(d._column)>e.indexOf(s));o.__parentRow.insertBefore(o,a)}}})),s._bodyContentHidden=n}),t&&this._frozenCellsChanged(),this._lazyColumns){const s=[...e].reverse().find(a=>a.frozen),n=this.__getColumnEnd(s),o=e.find(a=>!a.frozen&&!a._bodyContentHidden);this.__lazyColumnsStart=this.__getColumnStart(o)-n,this.$.items.style.setProperty("--_grid-lazy-columns-start",`${this.__lazyColumnsStart}px`),this._resetKeyboardNavigation()}}__getColumnEnd(e){return e?e._sizerCell.offsetLeft+(this.__isRTL?0:e._sizerCell.offsetWidth):this.__isRTL?this.$.table.clientWidth:0}__getColumnStart(e){return e?e._sizerCell.offsetLeft+(this.__isRTL?e._sizerCell.offsetWidth:0):this.__isRTL?this.$.table.clientWidth:0}__isColumnInViewport(e){return e.frozen||e.frozenToEnd?!0:this.__isHorizontallyInViewport(e._sizerCell)}__isHorizontallyInViewport(e){return e.offsetLeft+e.offsetWidth>=this._scrollLeft&&e.offsetLeft<=this._scrollLeft+this.clientWidth}__columnRenderingChanged(e,t){t==="eager"?this.$.scroller.removeAttribute("column-rendering"):this.$.scroller.setAttribute("column-rendering",t),this.__updateColumnsBodyContentHidden()}_updateOverflow(){this._debounceOverflow=x.debounce(this._debounceOverflow,j,()=>{this.__doUpdateOverflow()})}__doUpdateOverflow(){let e="";const t=this.$.table;t.scrollTop<t.scrollHeight-t.clientHeight&&(e+=" bottom"),t.scrollTop>0&&(e+=" top");const s=Le(t,this.getAttribute("dir"));s>0&&(e+=" start"),s<t.scrollWidth-t.clientWidth&&(e+=" end"),this.__isRTL&&(e=e.replace(/start|end/giu,o=>o==="start"?"end":"start")),t.scrollLeft<t.scrollWidth-t.clientWidth&&(e+=" right"),t.scrollLeft>0&&(e+=" left");const n=e.trim();n.length>0&&this.getAttribute("overflow")!==n?this.setAttribute("overflow",n):n.length===0&&this.hasAttribute("overflow")&&this.removeAttribute("overflow")}_frozenCellsChanged(){this._debouncerCacheElements=x.debounce(this._debouncerCacheElements,K,()=>{Array.from(this.shadowRoot.querySelectorAll('[part~="cell"]')).forEach(e=>{e.style.transform=""}),this._frozenCells=Array.prototype.slice.call(this.$.table.querySelectorAll("[frozen]")),this._frozenToEndCells=Array.prototype.slice.call(this.$.table.querySelectorAll("[frozen-to-end]")),this.__updateHorizontalScrollPosition()}),this._debounceUpdateFrozenColumn()}_debounceUpdateFrozenColumn(){this.__debounceUpdateFrozenColumn=x.debounce(this.__debounceUpdateFrozenColumn,K,()=>this._updateFrozenColumn())}_updateFrozenColumn(){if(!this._columnTree)return;const e=this._columnTree[this._columnTree.length-1].slice(0);e.sort((n,o)=>n._order-o._order);let t,s;for(let n=0;n<e.length;n++){const o=e[n];o._lastFrozen=!1,o._firstFrozenToEnd=!1,s===void 0&&o.frozenToEnd&&!o.hidden&&(s=n),o.frozen&&!o.hidden&&(t=n)}t!==void 0&&(e[t]._lastFrozen=!0),s!==void 0&&(e[s]._firstFrozenToEnd=!0),this.__updateColumnsBodyContentHidden()}__updateHorizontalScrollPosition(){if(!this._columnTree)return;const e=this.$.table.scrollWidth,t=this.$.table.clientWidth,s=Math.max(0,this.$.table.scrollLeft),n=Le(this.$.table,this.getAttribute("dir")),o=`translate(${-s}px, 0)`;this.$.header.style.transform=o,this.$.footer.style.transform=o,this.$.items.style.transform=o;const a=this.__isRTL?n+t-e:s,d=`translate(${a}px, 0)`;this._frozenCells.forEach(g=>{g.style.transform=d});const _=this.__isRTL?n:s+t-e,f=`translate(${_}px, 0)`;let p=f;if(this._lazyColumns){const g=this._getColumnsInOrder(),w=[...g].reverse().find(E=>!E.frozenToEnd&&!E._bodyContentHidden),I=this.__getColumnEnd(w),T=g.find(E=>E.frozenToEnd),C=this.__getColumnStart(T);p=`translate(${_+(C-I)+this.__lazyColumnsStart}px, 0)`}this._frozenToEndCells.forEach(g=>{this.$.items.contains(g)?g.style.transform=p:g.style.transform=f}),this.hasAttribute("navigating")&&this.__rowFocusMode&&this.$.table.style.setProperty("--_grid-horizontal-scroll-position",`${-a}px`)}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ws=r=>class extends r{static get properties(){return{selectedItems:{type:Object,notify:!0,value:()=>[],sync:!0},__selectedKeys:{type:Object,computed:"__computeSelectedKeys(itemIdPath, selectedItems)"}}}static get observers(){return["__selectedItemsChanged(itemIdPath, selectedItems)"]}_isSelected(e){return this.__selectedKeys.has(this.getItemId(e))}selectItem(e){this._isSelected(e)||(this.selectedItems=[...this.selectedItems,e])}deselectItem(e){this._isSelected(e)&&(this.selectedItems=this.selectedItems.filter(t=>!this._itemsEqual(t,e)))}_toggleItem(e){this._isSelected(e)?this.deselectItem(e):this.selectItem(e)}__selectedItemsChanged(){this.requestContentUpdate()}__computeSelectedKeys(e,t){const s=t||[],n=new Set;return s.forEach(o=>{n.add(this.getItemId(o))}),n}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */let ct="prepend";const Us=r=>class extends r{static get properties(){return{multiSort:{type:Boolean,value:!1},multiSortPriority:{type:String,value:()=>ct},multiSortOnShiftClick:{type:Boolean,value:!1},_sorters:{type:Array,value:()=>[]},_previousSorters:{type:Array,value:()=>[]}}}static setDefaultMultiSortPriority(e){ct=["append","prepend"].includes(e)?e:"prepend"}ready(){super.ready(),this.addEventListener("sorter-changed",this._onSorterChanged)}_onSorterChanged(e){const t=e.target;e.stopPropagation(),t._grid=this,this.__updateSorter(t,e.detail.shiftClick,e.detail.fromSorterClick),this.__applySorters()}__removeSorters(e){e.length!==0&&(this._sorters=this._sorters.filter(t=>e.indexOf(t)<0),this.multiSort&&this.__updateSortOrders(),this.__applySorters())}__updateSortOrders(){this._sorters.forEach((e,t)=>{e._order=this._sorters.length>1?t:null})}__appendSorter(e){e.direction?this._sorters.includes(e)||this._sorters.push(e):this._removeArrayItem(this._sorters,e),this.__updateSortOrders()}__prependSorter(e){this._removeArrayItem(this._sorters,e),e.direction&&this._sorters.unshift(e),this.__updateSortOrders()}__updateSorter(e,t,s){if(!(!e.direction&&this._sorters.indexOf(e)===-1)){if(e._order=null,this.multiSort&&(!this.multiSortOnShiftClick||!s)||this.multiSortOnShiftClick&&t)this.multiSortPriority==="append"?this.__appendSorter(e):this.__prependSorter(e);else if(e.direction||this.multiSortOnShiftClick){const n=this._sorters.filter(o=>o!==e);this._sorters=e.direction?[e]:[],n.forEach(o=>{o._order=null,o.direction=null})}}}__applySorters(){this.dataProvider&&this.isAttached&&JSON.stringify(this._previousSorters)!==JSON.stringify(this._mapSorters())&&this.__debounceClearCache(),this._a11yUpdateSorters(),this._previousSorters=this._mapSorters()}_mapSorters(){return this._sorters.map(e=>({path:e.path,direction:e.direction}))}_removeArrayItem(e,t){const s=e.indexOf(t);s>-1&&e.splice(s,1)}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const qs=r=>class extends r{static get properties(){return{cellClassNameGenerator:{type:Function,sync:!0},cellPartNameGenerator:{type:Function,sync:!0}}}static get observers(){return["__cellClassNameGeneratorChanged(cellClassNameGenerator)","__cellPartNameGeneratorChanged(cellPartNameGenerator)"]}__cellClassNameGeneratorChanged(){this.generateCellClassNames()}__cellPartNameGeneratorChanged(){this.generateCellPartNames()}generateCellClassNames(){O(this.$.items,e=>{!e.hidden&&!e.hasAttribute("loading")&&this._generateCellClassNames(e,this.__getRowModel(e))})}generateCellPartNames(){O(this.$.items,e=>{!e.hidden&&!e.hasAttribute("loading")&&this._generateCellPartNames(e,this.__getRowModel(e))})}_generateCellClassNames(e,t){oe(e,s=>{if(s.__generatedClasses&&s.__generatedClasses.forEach(n=>s.classList.remove(n)),this.cellClassNameGenerator){const n=this.cellClassNameGenerator(s._column,t);s.__generatedClasses=n&&n.split(" ").filter(o=>o.length>0),s.__generatedClasses&&s.__generatedClasses.forEach(o=>s.classList.add(o))}})}_generateCellPartNames(e,t){oe(e,s=>{if(s.__generatedParts&&s.__generatedParts.forEach(n=>{Y(s,null,n)}),this.cellPartNameGenerator){const n=this.cellPartNameGenerator(s._column,t);s.__generatedParts=n&&n.split(" ").filter(o=>o.length>0),s.__generatedParts&&s.__generatedParts.forEach(o=>{Y(s,!0,o)})}})}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const js=r=>class extends Rs(Ds($s(Ss(Gs(Ws(Us(Vs(Bs(Es(Ns(Os(ks(Hs(Ls(qs(gi(r))))))))))))))))){static get observers(){return["_columnTreeChanged(_columnTree)","_flatSizeChanged(_flatSize, __virtualizer, _hasData, _columnTree)"]}static get properties(){return{_safari:{type:Boolean,value:mt},_ios:{type:Boolean,value:ke},_firefox:{type:Boolean,value:bi},_android:{type:Boolean,value:Xe},_touchDevice:{type:Boolean,value:Ee},allRowsVisible:{type:Boolean,value:!1,reflectToAttribute:!0},__pendingRecalculateColumnWidths:{type:Boolean,value:!0},isAttached:{value:!1},__gridElement:{type:Boolean,value:!0}}}constructor(){super(),this.addEventListener("animationend",this._onAnimationEnd)}get _firstVisibleIndex(){const i=this.__getFirstVisibleItem();return i?i.index:void 0}get _lastVisibleIndex(){const i=this.__getLastVisibleItem();return i?i.index:void 0}connectedCallback(){super.connectedCallback(),this.isAttached=!0,this.recalculateColumnWidths()}disconnectedCallback(){super.disconnectedCallback(),this.isAttached=!1,this._hideTooltip(!0)}__getFirstVisibleItem(){return this._getRenderedRows().find(i=>this._isInViewport(i))}__getLastVisibleItem(){return this._getRenderedRows().reverse().find(i=>this._isInViewport(i))}_isInViewport(i){const e=this.$.table.getBoundingClientRect(),t=i.getBoundingClientRect(),s=this.$.header.getBoundingClientRect().height,n=this.$.footer.getBoundingClientRect().height;return t.bottom>e.top+s&&t.top<e.bottom-n}_getRenderedRows(){return Array.from(this.$.items.children).filter(i=>!i.hidden).sort((i,e)=>i.index-e.index)}_getRowContainingNode(i){const e=Ct("vaadin-grid-cell-content",i);return e?e.assignedSlot.parentElement.parentElement:void 0}_isItemAssignedToRow(i,e){const t=this.__getRowModel(e);return this.getItemId(i)===this.getItemId(t.item)}ready(){super.ready(),this.__virtualizer=new os({createElements:this._createScrollerRows.bind(this),updateElement:this._updateScrollerItem.bind(this),scrollContainer:this.$.items,scrollTarget:this.$.table,reorderElements:!0}),new ResizeObserver(()=>setTimeout(()=>{this.__updateColumnsBodyContentHidden(),this.__tryToRecalculateColumnWidthsIfPending()})).observe(this.$.table),Ge(this),this._tooltipController=new xt(this),this.addController(this._tooltipController),this._tooltipController.setManual(!0)}__getBodyCellCoordinates(i){if(this.$.items.contains(i)&&i.localName==="td")return{item:i.parentElement._item,column:i._column}}__focusBodyCell({item:i,column:e}){const t=this._getRenderedRows().find(n=>n._item===i),s=t&&[...t.children].find(n=>n._column===e);s&&s.focus()}_focusFirstVisibleRow(){const i=this.__getFirstVisibleItem();this.__rowFocusMode=!0,i.focus()}_flatSizeChanged(i,e,t,s){if(e&&t&&s){const n=this.shadowRoot.activeElement,o=this.__getBodyCellCoordinates(n),a=e.size||0;e.size=i,e.update(a-1,a-1),i<a&&e.update(i-1,i-1),o&&n.parentElement.hidden&&this.__focusBodyCell(o),this._resetKeyboardNavigation()}}__hasRowsWithClientHeight(){return!!Array.from(this.$.items.children).filter(i=>i.clientHeight).length}__getIntrinsicWidth(i){return this.__intrinsicWidthCache.has(i)||this.__calculateAndCacheIntrinsicWidths([i]),this.__intrinsicWidthCache.get(i)}__getDistributedWidth(i,e){if(i==null||i===this)return 0;const t=Math.max(this.__getIntrinsicWidth(i),this.__getDistributedWidth((i.assignedSlot||i).parentElement,i));if(!e)return t;const s=i,n=t,o=s._visibleChildColumns.map(f=>this.__getIntrinsicWidth(f)).reduce((f,p)=>f+p,0),a=Math.max(0,n-o),_=this.__getIntrinsicWidth(e)/o*a;return this.__getIntrinsicWidth(e)+_}_recalculateColumnWidths(i){this.__virtualizer.flush(),[...this.$.header.children,...this.$.footer.children].forEach(s=>{s.__debounceUpdateHeaderFooterRowVisibility&&s.__debounceUpdateHeaderFooterRowVisibility.flush()}),this._debouncerHiddenChanged&&this._debouncerHiddenChanged.flush(),this.__intrinsicWidthCache=new Map;const e=this._firstVisibleIndex,t=this._lastVisibleIndex;this.__viewportRowsCache=this._getRenderedRows().filter(s=>s.index>=e&&s.index<=t),this.__calculateAndCacheIntrinsicWidths(i),i.forEach(s=>{s.width=`${this.__getDistributedWidth(s)}px`})}__setVisibleCellContentAutoWidth(i,e){i._allCells.filter(t=>this.$.items.contains(t)?this.__viewportRowsCache.includes(t.parentElement):!0).forEach(t=>{t.__measuringAutoWidth=e,t.__measuringAutoWidth?(t.__originalWidth=t.style.width,t.style.width="auto",t.style.position="absolute"):(t.style.width=t.__originalWidth,delete t.__originalWidth,t.style.position="")})}__getAutoWidthCellsMaxWidth(i){return i._allCells.reduce((e,t)=>t.__measuringAutoWidth?Math.max(e,t.offsetWidth+1):e,0)}__calculateAndCacheIntrinsicWidths(i){i.forEach(e=>this.__setVisibleCellContentAutoWidth(e,!0)),i.forEach(e=>{const t=this.__getAutoWidthCellsMaxWidth(e);this.__intrinsicWidthCache.set(e,t)}),i.forEach(e=>this.__setVisibleCellContentAutoWidth(e,!1))}recalculateColumnWidths(){if(!this._columnTree)return;if(re(this)||this._dataProviderController.isLoading()){this.__pendingRecalculateColumnWidths=!0;return}const i=this._getColumns().filter(e=>!e.hidden&&e.autoWidth);this._recalculateColumnWidths(i)}__tryToRecalculateColumnWidthsIfPending(){this.__pendingRecalculateColumnWidths&&!re(this)&&!this._dataProviderController.isLoading()&&this.__hasRowsWithClientHeight()&&(this.__pendingRecalculateColumnWidths=!1,this.recalculateColumnWidths())}_onDataProviderPageLoaded(){super._onDataProviderPageLoaded(),this.__tryToRecalculateColumnWidthsIfPending()}_createScrollerRows(i){const e=[];for(let t=0;t<i;t++){const s=document.createElement("tr");s.setAttribute("part","row body-row"),s.setAttribute("role","row"),s.setAttribute("tabindex","-1"),this._columnTree&&this._updateRow(s,this._columnTree[this._columnTree.length-1],"body",!1,!0),e.push(s)}return this._columnTree&&this._columnTree[this._columnTree.length-1].forEach(t=>{t.isConnected&&t._cells&&(t._cells=[...t._cells])}),this.__afterCreateScrollerRowsDebouncer=x.debounce(this.__afterCreateScrollerRowsDebouncer,j,()=>{this._afterScroll(),this.__tryToRecalculateColumnWidthsIfPending()}),e}_createCell(i,e){const s=`vaadin-grid-cell-content-${this._contentIndex=this._contentIndex+1||0}`,n=document.createElement("vaadin-grid-cell-content");n.setAttribute("slot",s);const o=document.createElement(i);o.id=s.replace("-content-","-"),o.setAttribute("role",i==="td"?"gridcell":"columnheader"),!Xe&&!ke&&(o.addEventListener("mouseenter",d=>{this.$.scroller.hasAttribute("scrolling")||this._showTooltip(d)}),o.addEventListener("mouseleave",()=>{this._hideTooltip()}),o.addEventListener("mousedown",()=>{this._hideTooltip(!0)}));const a=document.createElement("slot");if(a.setAttribute("name",s),e&&e._focusButtonMode){const d=document.createElement("div");d.setAttribute("role","button"),d.setAttribute("tabindex","-1"),o.appendChild(d),o._focusButton=d,o.focus=function(){o._focusButton.focus()},d.appendChild(a)}else o.setAttribute("tabindex","-1"),o.appendChild(a);return o._content=n,n.addEventListener("mousedown",()=>{if(vi){const d=_=>{const f=n.contains(this.getRootNode().activeElement),p=_.composedPath().includes(n);!f&&p&&o.focus(),document.removeEventListener("mouseup",d,!0)};document.addEventListener("mouseup",d,!0)}else setTimeout(()=>{n.contains(this.getRootNode().activeElement)||o.focus()})}),o}_updateRow(i,e,t="body",s=!1,n=!1){const o=document.createDocumentFragment();oe(i,a=>{a._vacant=!0}),i.innerHTML="",t==="body"&&(i.__cells=[],i.__detailsCell=null),e.filter(a=>!a.hidden).forEach((a,d,_)=>{let f;if(t==="body"){if(a._cells||(a._cells=[]),f=a._cells.find(p=>p._vacant),f||(f=this._createCell("td",a),a._cells.push(f)),f.setAttribute("part","cell body-cell"),f.__parentRow=i,i.__cells.push(f),a._bodyContentHidden||i.appendChild(f),i===this.$.sizer&&(a._sizerCell=f),d===_.length-1&&this.rowDetailsRenderer){this._detailsCells||(this._detailsCells=[]);const p=this._detailsCells.find(g=>g._vacant)||this._createCell("td");this._detailsCells.indexOf(p)===-1&&this._detailsCells.push(p),p._content.parentElement||o.appendChild(p._content),this._configureDetailsCell(p),i.appendChild(p),i.__detailsCell=p,this._a11ySetRowDetailsCell(i,p),p._vacant=!1}n||(a._cells=[...a._cells])}else{const p=t==="header"?"th":"td";s||a.localName==="vaadin-grid-column-group"?(f=a[`_${t}Cell`]||this._createCell(p),f._column=a,i.appendChild(f),a[`_${t}Cell`]=f):(a._emptyCells||(a._emptyCells=[]),f=a._emptyCells.find(g=>g._vacant)||this._createCell(p),f._column=a,i.appendChild(f),a._emptyCells.indexOf(f)===-1&&a._emptyCells.push(f)),f.part.add("cell",`${t}-cell`)}f._content.parentElement||o.appendChild(f._content),f._vacant=!1,f._column=a}),t!=="body"&&this.__debounceUpdateHeaderFooterRowVisibility(i),this.appendChild(o),this._frozenCellsChanged(),this._updateFirstAndLastColumnForRow(i)}__debounceUpdateHeaderFooterRowVisibility(i){i.__debounceUpdateHeaderFooterRowVisibility=x.debounce(i.__debounceUpdateHeaderFooterRowVisibility,K,()=>this.__updateHeaderFooterRowVisibility(i))}__updateHeaderFooterRowVisibility(i){if(!i)return;const e=Array.from(i.children).filter(t=>{const s=t._column;if(s._emptyCells&&s._emptyCells.indexOf(t)>-1)return!1;if(i.parentElement===this.$.header){if(s.headerRenderer)return!0;if(s.header===null)return!1;if(s.path||s.header!==void 0)return!0}else if(s.footerRenderer)return!0;return!1});i.hidden!==!e.length&&(i.hidden=!e.length),this._resetKeyboardNavigation()}_updateScrollerItem(i,e){this._preventScrollerRotatingCellFocus(i,e),this._columnTree&&(this._updateRowOrderParts(i,e),this._a11yUpdateRowRowindex(i,e),this._getItem(e,i))}_columnTreeChanged(i){this._renderColumnTree(i),this.recalculateColumnWidths(),this.__updateColumnsBodyContentHidden()}_updateRowOrderParts(i,e=i.index){_e(i,{first:e===0,last:e===this._flatSize-1,odd:e%2!==0,even:e%2===0})}_updateRowStateParts(i,{expanded:e,selected:t,detailsOpened:s}){_e(i,{expanded:e,collapsed:this.__isRowExpandable(i),selected:t,"details-opened":s})}_renderColumnTree(i){for(O(this.$.items,e=>{this._updateRow(e,i[i.length-1],"body",!1,!0);const t=this.__getRowModel(e);this._updateRowOrderParts(e),this._updateRowStateParts(e,t),this._filterDragAndDrop(e,t)});this.$.header.children.length<i.length;){const e=document.createElement("tr");e.setAttribute("part","row"),e.setAttribute("role","row"),e.setAttribute("tabindex","-1"),this.$.header.appendChild(e);const t=document.createElement("tr");t.setAttribute("part","row"),t.setAttribute("role","row"),t.setAttribute("tabindex","-1"),this.$.footer.appendChild(t)}for(;this.$.header.children.length>i.length;)this.$.header.removeChild(this.$.header.firstElementChild),this.$.footer.removeChild(this.$.footer.firstElementChild);O(this.$.header,(e,t,s)=>{this._updateRow(e,i[t],"header",t===i.length-1);const n=ne(e);U(n,"first-header-row-cell",t===0),U(n,"last-header-row-cell",t===s.length-1)}),O(this.$.footer,(e,t,s)=>{this._updateRow(e,i[i.length-1-t],"footer",t===0);const n=ne(e);U(n,"first-footer-row-cell",t===0),U(n,"last-footer-row-cell",t===s.length-1)}),this._updateRow(this.$.sizer,i[i.length-1]),this._resizeHandler(),this._frozenCellsChanged(),this._updateFirstAndLastColumn(),this._resetKeyboardNavigation(),this._a11yUpdateHeaderRows(),this._a11yUpdateFooterRows(),this.generateCellClassNames(),this.generateCellPartNames(),this.__updateHeaderAndFooter()}_updateItem(i,e){i._item=e;const t=this.__getRowModel(i);this._toggleDetailsCell(i,t.detailsOpened),this._a11yUpdateRowLevel(i,t.level),this._a11yUpdateRowSelected(i,t.selected),this._updateRowStateParts(i,t),this._generateCellClassNames(i,t),this._generateCellPartNames(i,t),this._filterDragAndDrop(i,t),O(i,s=>{if(s._renderer){const n=s._column||this;s._renderer.call(n,s._content,n,t)}}),this._updateDetailsCellHeight(i),this._a11yUpdateRowExpanded(i,t.expanded)}_resizeHandler(){this._updateDetailsCellHeights(),this.__updateHorizontalScrollPosition()}_onAnimationEnd(i){i.animationName.indexOf("vaadin-grid-appear")===0&&(i.stopPropagation(),this.__tryToRecalculateColumnWidthsIfPending(),requestAnimationFrame(()=>{this.__scrollToPendingIndexes()}))}__getRowModel(i){return{index:i.index,item:i._item,level:this._getIndexLevel(i.index),expanded:this._isExpanded(i._item),selected:this._isSelected(i._item),detailsOpened:!!this.rowDetailsRenderer&&this._isDetailsOpened(i._item)}}_showTooltip(i){const e=this._tooltipController.node;e&&e.isConnected&&(this._tooltipController.setTarget(i.target),this._tooltipController.setContext(this.getEventContext(i)),e._stateController.open({focus:i.type==="focusin",hover:i.type==="mouseenter"}))}_hideTooltip(i){const e=this._tooltipController&&this._tooltipController.node;e&&e._stateController.close(i)}requestContentUpdate(){this.__updateHeaderAndFooter(),this.__updateVisibleRows()}__updateHeaderAndFooter(){(this._columnTree||[]).forEach(i=>{i.forEach(e=>{e._renderHeaderAndFooter&&e._renderHeaderAndFooter()})})}__updateVisibleRows(i,e){this.__virtualizer&&this.__virtualizer.update(i,e)}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ks=F`
  @keyframes vaadin-grid-appear {
    to {
      opacity: 1;
    }
  }

  :host {
    display: flex;
    flex-direction: column;
    animation: 1ms vaadin-grid-appear;
    height: 400px;
    flex: 1 1 auto;
    align-self: stretch;
    position: relative;
  }

  :host([hidden]) {
    display: none !important;
  }

  :host([disabled]) {
    pointer-events: none;
  }

  #scroller {
    display: flex;
    flex-direction: column;
    min-height: 100%;
    transform: translateY(0);
    width: auto;
    height: auto;
    position: absolute;
    inset: 0;
  }

  :host([all-rows-visible]) {
    height: auto;
    align-self: flex-start;
    flex-grow: 0;
    width: 100%;
  }

  :host([all-rows-visible]) #scroller {
    width: 100%;
    height: 100%;
    position: relative;
  }

  :host([all-rows-visible]) #items {
    min-height: 1px;
  }

  #table {
    display: flex;
    flex-direction: column;
    width: 100%;
    height: 100%;
    overflow: auto;
    position: relative;
    outline: none;
    /* Workaround for a Desktop Safari bug: new stacking context here prevents the scrollbar from getting hidden */
    z-index: 0;
  }

  #header,
  #footer {
    display: block;
    position: -webkit-sticky;
    position: sticky;
    left: 0;
    overflow: visible;
    width: 100%;
    z-index: 1;
  }

  #header {
    top: 0;
  }

  th {
    text-align: inherit;
  }

  /* Safari doesn't work with "inherit" */
  [safari] th {
    text-align: initial;
  }

  #footer {
    bottom: 0;
  }

  #items {
    flex-grow: 1;
    flex-shrink: 0;
    display: block;
    position: -webkit-sticky;
    position: sticky;
    width: 100%;
    left: 0;
    overflow: visible;
  }

  [part~='row'] {
    display: flex;
    width: 100%;
    box-sizing: border-box;
    margin: 0;
  }

  [part~='row'][loading] [part~='body-cell'] ::slotted(vaadin-grid-cell-content) {
    visibility: hidden;
  }

  [column-rendering='lazy'] [part~='body-cell']:not([frozen]):not([frozen-to-end]) {
    transform: translateX(var(--_grid-lazy-columns-start));
  }

  #items [part~='row'] {
    position: absolute;
  }

  #items [part~='row']:empty {
    height: 100%;
  }

  [part~='cell']:not([part~='details-cell']) {
    flex-shrink: 0;
    flex-grow: 1;
    box-sizing: border-box;
    display: flex;
    width: 100%;
    position: relative;
    align-items: center;
    padding: 0;
    white-space: nowrap;
  }

  [part~='cell'] > [tabindex] {
    display: flex;
    align-items: inherit;
    outline: none;
    position: absolute;
    inset: 0;
  }

  [part~='details-cell'] {
    position: absolute;
    bottom: 0;
    width: 100%;
    box-sizing: border-box;
    padding: 0;
  }

  [part~='cell'] ::slotted(vaadin-grid-cell-content) {
    display: block;
    width: 100%;
    box-sizing: border-box;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  [hidden] {
    display: none !important;
  }

  [frozen],
  [frozen-to-end] {
    z-index: 2;
    will-change: transform;
  }

  [no-scrollbars][safari] #table,
  [no-scrollbars][firefox] #table {
    overflow: hidden;
  }

  /* Reordering styles */
  :host([reordering]) [part~='cell'] ::slotted(vaadin-grid-cell-content),
  :host([reordering]) [part~='resize-handle'],
  #scroller[no-content-pointer-events] [part~='cell'] ::slotted(vaadin-grid-cell-content) {
    pointer-events: none;
  }

  [part~='reorder-ghost'] {
    visibility: hidden;
    position: fixed;
    pointer-events: none;
    opacity: 0.5;

    /* Prevent overflowing the grid in Firefox */
    top: 0;
    left: 0;
  }

  :host([reordering]) {
    -moz-user-select: none;
    -webkit-user-select: none;
    user-select: none;
  }

  /* Resizing styles */
  [part~='resize-handle'] {
    position: absolute;
    top: 0;
    right: 0;
    height: 100%;
    cursor: col-resize;
    z-index: 1;
  }

  [part~='resize-handle']::before {
    position: absolute;
    content: '';
    height: 100%;
    width: 35px;
    transform: translateX(-50%);
  }

  [last-column] [part~='resize-handle']::before,
  [last-frozen] [part~='resize-handle']::before {
    width: 18px;
    transform: none;
    right: 0;
  }

  [frozen-to-end] [part~='resize-handle'] {
    left: 0;
    right: auto;
  }

  [frozen-to-end] [part~='resize-handle']::before {
    left: 0;
    right: auto;
  }

  [first-frozen-to-end] [part~='resize-handle']::before {
    width: 18px;
    transform: none;
  }

  [first-frozen-to-end] {
    margin-inline-start: auto;
  }

  /* Hide resize handle if scrolled to end */
  :host(:not([overflow~='end'])) [first-frozen-to-end] [part~='resize-handle'] {
    display: none;
  }

  #scroller[column-resizing] {
    -ms-user-select: none;
    -moz-user-select: none;
    -webkit-user-select: none;
    user-select: none;
  }

  /* Sizer styles */
  #sizer {
    display: flex;
    position: absolute;
    visibility: hidden;
  }

  #sizer [part~='details-cell'] {
    display: none !important;
  }

  #sizer [part~='cell'][hidden] {
    display: none !important;
  }

  #sizer [part~='cell'] {
    display: block;
    flex-shrink: 0;
    line-height: 0;
    height: 0 !important;
    min-height: 0 !important;
    max-height: 0 !important;
    padding: 0 !important;
    border: none !important;
  }

  #sizer [part~='cell']::before {
    content: '-';
  }

  #sizer [part~='cell'] ::slotted(vaadin-grid-cell-content) {
    display: none !important;
  }

  /* RTL specific styles */

  :host([dir='rtl']) #items,
  :host([dir='rtl']) #header,
  :host([dir='rtl']) #footer {
    left: auto;
  }

  :host([dir='rtl']) [part~='reorder-ghost'] {
    left: auto;
    right: 0;
  }

  :host([dir='rtl']) [part~='resize-handle'] {
    left: 0;
    right: auto;
  }

  :host([dir='rtl']) [part~='resize-handle']::before {
    transform: translateX(50%);
  }

  :host([dir='rtl']) [last-column] [part~='resize-handle']::before,
  :host([dir='rtl']) [last-frozen] [part~='resize-handle']::before {
    left: 0;
    right: auto;
  }

  :host([dir='rtl']) [frozen-to-end] [part~='resize-handle'] {
    right: 0;
    left: auto;
  }

  :host([dir='rtl']) [frozen-to-end] [part~='resize-handle']::before {
    right: 0;
    left: auto;
  }

  @media (forced-colors: active) {
    [part~='selected-row'] [part~='first-column-cell']::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      bottom: 0;
      border: 2px solid;
    }

    [part~='focused-cell']::before {
      outline: 2px solid !important;
      outline-offset: -1px;
    }
  }
`;/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */k("vaadin-grid",Ks,{moduleId:"vaadin-grid-styles"});class ce extends js(He(ae($e(L)))){static get template(){return Q`
      <div
        id="scroller"
        safari$="[[_safari]]"
        ios$="[[_ios]]"
        loading$="[[loading]]"
        column-reordering-allowed$="[[columnReorderingAllowed]]"
      >
        <table id="table" role="treegrid" aria-multiselectable="true" tabindex="0">
          <caption id="sizer" part="row"></caption>
          <thead id="header" role="rowgroup"></thead>
          <tbody id="items" role="rowgroup"></tbody>
          <tfoot id="footer" role="rowgroup"></tfoot>
        </table>

        <div part="reorder-ghost"></div>
      </div>

      <slot name="tooltip"></slot>

      <div id="focusexit" tabindex="0"></div>
    `}static get is(){return"vaadin-grid"}}V(ce);k("vaadin-grid-sorter",F`
    :host {
      justify-content: flex-start;
      align-items: baseline;
      -webkit-user-select: none;
      -moz-user-select: none;
      user-select: none;
      cursor: var(--lumo-clickable-cursor);
    }

    [part='content'] {
      display: inline-block;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    [part='indicators'] {
      margin-left: var(--lumo-space-s);
    }

    [part='indicators']::before {
      transform: scale(0.8);
    }

    :host(:not([direction]):not(:hover)) [part='indicators'] {
      color: var(--lumo-tertiary-text-color);
    }

    :host([direction]) {
      color: var(--vaadin-selection-color-text, var(--lumo-primary-text-color));
    }

    [part='order'] {
      font-size: var(--lumo-font-size-xxs);
      line-height: 1;
    }

    /* RTL specific styles */

    :host([dir='rtl']) [part='indicators'] {
      margin-right: var(--lumo-space-s);
      margin-left: 0;
    }
  `,{moduleId:"lumo-grid-sorter"});/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Gt=document.createElement("template");Gt.innerHTML=`
  <style>
    @font-face {
      font-family: 'vaadin-grid-sorter-icons';
      src: url(data:application/font-woff;charset=utf-8;base64,d09GRgABAAAAAAQwAA0AAAAABuwAAQAAAAAAAAAAAAAAAAAAAAAAAAAAAABGRlRNAAAEFAAAABkAAAAcfep+mUdERUYAAAP4AAAAHAAAAB4AJwAOT1MvMgAAAZgAAAA/AAAAYA8TBPpjbWFwAAAB7AAAAFUAAAFeF1fZ4mdhc3AAAAPwAAAACAAAAAgAAAAQZ2x5ZgAAAlgAAABcAAAAnMvguMloZWFkAAABMAAAAC8AAAA2C5Ap72hoZWEAAAFgAAAAHQAAACQGbQPHaG10eAAAAdgAAAAUAAAAHAoAAABsb2NhAAACRAAAABIAAAASAIwAYG1heHAAAAGAAAAAFgAAACAACwAKbmFtZQAAArQAAAECAAACZxWCgKhwb3N0AAADuAAAADUAAABZCrApUXicY2BkYGAA4rDECVrx/DZfGbhZGEDgyqNPOxH0/wNMq5kPALkcDEwgUQBWRA0dAHicY2BkYGA+8P8AAwMLAwgwrWZgZEAFbABY4QM8AAAAeJxjYGRgYOAAQiYGEICQSAAAAi8AFgAAeJxjYGY6yziBgZWBgWkm0xkGBoZ+CM34msGYkZMBFTAKoAkwODAwvmRiPvD/AIMDMxCD1CDJKjAwAgBktQsXAHicY2GAAMZQCM0EwqshbAALxAEKeJxjYGBgZoBgGQZGBhCIAPIYwXwWBhsgzcXAwcAEhIwMCi+Z/v/9/x+sSuElA4T9/4k4K1gHFwMMMILMY2QDYmaoABOQYGJABUA7WBiGNwAAJd4NIQAAAAAAAAAACAAIABAAGAAmAEAATgAAeJyNjLENgDAMBP9tIURJwQCMQccSZgk2i5fIYBDAidJjycXr7x5EPwE2wY8si7jmyBNXGo/bNBerxJNrpxhbO3/fEFpx8ZICpV+ghxJ74fAMe+h7Ox14AbrsHB14nK2QQWrDMBRER4mTkhQK3ZRQKOgCNk7oGQqhhEIX2WSlWEI1BAlkJ5CDdNsj5Ey9Rncdi38ES+jzNJo/HwTgATcoDEthhY3wBHc4CE+pfwsX5F/hGe7Vo/AcK/UhvMSz+mGXKhZU6pww8ISz3oWn1BvhgnwTnuEJf8Jz1OpFeIlX9YULDLdFi4ASHolkSR0iuYdjLak1vAequBhj21D61Nqyi6l3qWybGPjySbPHGScGJl6dP58MYcQRI0bts7mjebBqrFENH7t3qWtj0OuqHnXcW7b0HOTZFnKryRGW2hFX1m0O2vEM3opNMfTau+CS6Z3Vx6veNnEXY6jwDxhsc2gAAHicY2BiwA84GBgYmRiYGJkZmBlZGFkZ2djScyoLMgzZS/MyDQwMwLSrpYEBlIbxjQDrzgsuAAAAAAEAAf//AA94nGNgZGBg4AFiMSBmYmAEQnYgZgHzGAAD6wA2eJxjYGBgZACCKyoz1cD0o087YTQATOcIewAAAA==) format('woff');
      font-weight: normal;
      font-style: normal;
    }
  </style>
`;document.head.appendChild(Gt.content);k("vaadin-grid-sorter",F`
    :host {
      display: inline-flex;
      cursor: pointer;
      max-width: 100%;
    }

    [part='content'] {
      flex: 1 1 auto;
    }

    [part='indicators'] {
      position: relative;
      align-self: center;
      flex: none;
    }

    [part='order'] {
      display: inline;
      vertical-align: super;
    }

    [part='indicators']::before {
      font-family: 'vaadin-grid-sorter-icons';
      display: inline-block;
    }

    :host(:not([direction])) [part='indicators']::before {
      content: '\\e901';
    }

    :host([direction='asc']) [part='indicators']::before {
      content: '\\e900';
    }

    :host([direction='desc']) [part='indicators']::before {
      content: '\\e902';
    }
  `,{moduleId:"vaadin-grid-sorter-styles"});const Ys=r=>class extends r{static get properties(){return{path:String,direction:{type:String,reflectToAttribute:!0,notify:!0,value:null,sync:!0},_order:{type:Number,value:null,sync:!0},_isConnected:{type:Boolean,observer:"__isConnectedChanged"}}}static get observers(){return["_pathOrDirectionChanged(path, direction)"]}ready(){super.ready(),this.addEventListener("click",this._onClick.bind(this))}connectedCallback(){super.connectedCallback(),this._isConnected=!0}disconnectedCallback(){super.disconnectedCallback(),this._isConnected=!1,!this.parentNode&&this._grid&&this._grid.__removeSorters([this])}_pathOrDirectionChanged(){this.__dispatchSorterChangedEvenIfPossible()}__isConnectedChanged(e,t){t!==!1&&this.__dispatchSorterChangedEvenIfPossible()}__dispatchSorterChangedEvenIfPossible(){this.path===void 0||this.direction===void 0||!this._isConnected||(this.dispatchEvent(new CustomEvent("sorter-changed",{detail:{shiftClick:!!this._shiftClick,fromSorterClick:!!this._fromSorterClick},bubbles:!0,composed:!0})),this._fromSorterClick=!1,this._shiftClick=!1)}_getDisplayOrder(e){return e===null?"":e+1}_onClick(e){if(e.defaultPrevented)return;const t=this.getRootNode().activeElement;this!==t&&this.contains(t)||(e.preventDefault(),this._shiftClick=e.shiftKey,this._fromSorterClick=!0,this.direction==="asc"?this.direction="desc":this.direction==="desc"?this.direction=null:this.direction="asc")}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class Qs extends Ys(ae(pe(L))){static get template(){return Q`
      <div part="content">
        <slot></slot>
      </div>
      <div part="indicators">
        <span part="order">[[_getDisplayOrder(_order)]]</span>
      </div>
    `}static get is(){return"vaadin-grid-sorter"}}V(Qs);/**
 * @license
 * Copyright (c) 2021 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Xs=Ie(r=>class extends Ci(yi(xi(r))){static get properties(){return{checked:{type:Boolean,value:!1,notify:!0,reflectToAttribute:!0}}}static get delegateProps(){return[...super.delegateProps,"checked"]}_onChange(e){const t=e.target;this._toggleChecked(t.checked)}_toggleChecked(e){this.checked=e}});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Zs=r=>class extends wi(Xs(Ai(vt(r)))){static get properties(){return{indeterminate:{type:Boolean,notify:!0,value:!1,reflectToAttribute:!0},name:{type:String,value:""},tabindex:{type:Number,value:0,reflectToAttribute:!0}}}static get delegateProps(){return[...super.delegateProps,"indeterminate"]}static get delegateAttrs(){return[...super.delegateAttrs,"name"]}constructor(){super(),this._setType("checkbox"),this.value="on"}ready(){super.ready(),this.addController(new Ii(this,e=>{this._setInputElement(e),this._setFocusElement(e),this.stateTarget=e,this.ariaTarget=e})),this.addController(new Ei(this.inputElement,this._labelController))}_shouldSetActive(e){return e.target.localName==="a"?!1:super._shouldSetActive(e)}_toggleChecked(e){this.indeterminate&&(this.indeterminate=!1),super._toggleChecked(e)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Js=F`
  :host {
    display: inline-block;
  }

  :host([hidden]) {
    display: none !important;
  }

  :host([disabled]) {
    -webkit-tap-highlight-color: transparent;
  }

  .vaadin-checkbox-container {
    display: grid;
    grid-template-columns: auto 1fr;
    align-items: baseline;
  }

  [part='checkbox'],
  ::slotted(input),
  ::slotted(label) {
    grid-row: 1;
  }

  [part='checkbox'],
  ::slotted(input) {
    grid-column: 1;
  }

  [part='checkbox'] {
    width: var(--vaadin-checkbox-size, 1em);
    height: var(--vaadin-checkbox-size, 1em);
    --_input-border-width: var(--vaadin-input-field-border-width, 0);
    --_input-border-color: var(--vaadin-input-field-border-color, transparent);
    box-shadow: inset 0 0 0 var(--_input-border-width, 0) var(--_input-border-color);
  }

  [part='checkbox']::before {
    display: block;
    content: '\\202F';
    line-height: var(--vaadin-checkbox-size, 1em);
    contain: paint;
  }

  /* visually hidden */
  ::slotted(input) {
    opacity: 0;
    cursor: inherit;
    margin: 0;
    align-self: stretch;
    -webkit-appearance: none;
    width: initial;
    height: initial;
  }

  @media (forced-colors: active) {
    [part='checkbox'] {
      outline: 1px solid;
      outline-offset: -1px;
    }

    :host([disabled]) [part='checkbox'],
    :host([disabled]) [part='checkbox']::after {
      outline-color: GrayText;
    }

    :host(:is([checked], [indeterminate])) [part='checkbox']::after {
      outline: 1px solid;
      outline-offset: -1px;
      border-radius: inherit;
    }

    :host([focused]) [part='checkbox'],
    :host([focused]) [part='checkbox']::after {
      outline-width: 2px;
    }
  }
`;/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */k("vaadin-checkbox",Js,{moduleId:"vaadin-checkbox-styles"});class er extends Zs(He(ae(L))){static get is(){return"vaadin-checkbox"}static get template(){return Q`
      <div class="vaadin-checkbox-container">
        <div part="checkbox" aria-hidden="true"></div>
        <slot name="input"></slot>
        <slot name="label"></slot>
      </div>
      <slot name="tooltip"></slot>
    `}ready(){super.ready(),this._tooltipController=new xt(this),this._tooltipController.setAriaTarget(this.inputElement),this.addController(this._tooltipController)}}V(er);(function(){const r=function(i){return window.Vaadin.Flow.tryCatchWrapper(i,"Vaadin Grid")};window.Vaadin.Flow.gridConnector={initLazy:i=>r(function(e){if(e.$connector)return;const t=e._dataProviderController;t.ensureFlatIndexHierarchyOriginal=t.ensureFlatIndexHierarchy,t.ensureFlatIndexHierarchy=r(function(l){const{item:c}=this.getFlatIndexContext(l);if(!c||!this.isExpanded(c))return;e.$connector.hasCacheForParentKey(e.getItemId(c))?this.ensureFlatIndexHierarchyOriginal(l):e.$connector.beforeEnsureFlatIndexHierarchy(l,c)}),t.isLoadingOriginal=t.isLoading,t.isLoading=r(function(){return e.$connector.hasEnsureSubCacheQueue()||this.isLoadingOriginal()});const s={},n={},o={},a=50,d=20;let _=[],f,p=[],g;const w=150;let I,T={};const C="null";T[C]=[0,0];const G=["SINGLE","NONE","MULTI"];let E={},M="SINGLE",H=!1;e.size=0,e.itemIdPath="key";function S(l){return{[e.itemIdPath]:l}}e.$connector={},e.$connector.hasCacheForParentKey=r(l=>o[l]!==void 0&&o[l].size!==void 0),e.$connector.hasEnsureSubCacheQueue=r(()=>p.length>0),e.$connector.hasParentRequestQueue=r(()=>_.length>0),e.$connector.hasRootRequestQueue=r(()=>Object.keys(s).length>0||!!I&&I.isActive()),e.$connector.beforeEnsureFlatIndexHierarchy=r(function(l,c){p.push({flatIndex:l,itemkey:e.getItemId(c)}),g=N.debounce(g,Ze,()=>{for(;p.length;)e.$connector.flushEnsureSubCache()})}),e.$connector.doSelection=r(function(l,c){M==="NONE"||!l.length||c&&e.hasAttribute("disabled")||(M==="SINGLE"&&(E={}),l.forEach(h=>{h&&(E[h.key]=h,h.selected=!0,c&&e.$server.select(h.key));const u=!e.activeItem||!h||h.key!=e.activeItem.key;!c&&M==="SINGLE"&&u&&(e.activeItem=h)}),e.selectedItems=Object.values(E))}),e.$connector.doDeselection=r(function(l,c){if(M==="NONE"||!l.length||c&&e.hasAttribute("disabled"))return;const h=e.selectedItems.slice();for(;l.length;){const u=l.shift();for(let m=0;m<h.length;m++){const v=h[m];if(u&&u.key===v.key){h.splice(m,1);break}}u&&(delete E[u.key],delete u.selected,c&&e.$server.deselect(u.key))}e.selectedItems=h}),e.__activeItemChanged=r(function(l,c){M=="SINGLE"&&(l?E[l.key]||e.$connector.doSelection([l],!0):c&&E[c.key]&&(e.__deselectDisallowed?e.activeItem=c:e.$connector.doDeselection([c],!0)))}),e._createPropertyObserver("activeItem","__activeItemChanged",!0),e.__activeItemChangedDetails=r(function(l,c){e.__disallowDetailsOnClick||l==null&&c===void 0||(l&&!l.detailsOpened?e.$server.setDetailsVisible(l.key):e.$server.setDetailsVisible(null))}),e._createPropertyObserver("activeItem","__activeItemChangedDetails",!0),e.$connector._getSameLevelPage=r(function(l,c,h){if((c.parentItem?e.getItemId(c.parentItem):C)===l)return Math.floor(h/e.pageSize);const{parentCache:m,parentCacheIndex:v}=c;return m?this._getSameLevelPage(l,m,v):null}),e.$connector.flushEnsureSubCache=r(function(){const l=p.shift();return l?(t.ensureFlatIndexHierarchyOriginal(l.flatIndex),!0):!1}),e.$connector.flushParentRequests=r(function(){let l=_.splice(0,d);return l.length?(e.$server.setParentRequestedRanges(l),!0):!1}),e.$connector.beforeParentRequest=r(function(l,c,h){_.push({firstIndex:l,size:c,parentKey:h}),f=N.debounce(f,Je.after(a),()=>{for(;_.length;)e.$connector.flushParentRequests()})}),e.$connector.fetchPage=r(function(l,c,h){const u=e._getRenderedRows();let m=u.length>0?u[0].index:0,v=u.length>0?u[u.length-1].index:0,b=v-m,A=Math.max(0,m-b),y=Math.min(v+b,e._flatSize),R=c,D=c;for(let le=A;le<=y;le++){const{cache:Wt,index:Ut}=t.getFlatIndexContext(le),Re=e.$connector._getSameLevelPage(h,Wt,Ut);Re!==null&&(R=Math.min(R,Re),D=Math.max(D,Re))}let Z=Math.max(0,R),J=h!==C?D:Math.min(D,Math.floor(e.size/e.pageSize)),ie=T[h];if(ie||(ie=[-1,-1]),ie[0]!=Z||ie[1]!=J){ie=[Z,J],T[h]=ie;let le=J-Z+1;l(Z*e.pageSize,le*e.pageSize)}}),e.dataProvider=r(function(l,c){if(l.pageSize!=e.pageSize)throw"Invalid pageSize";let h=l.page;if(l.parentItem){let u=e.getItemId(l.parentItem);n[u]||(n[u]={});const m=t.getItemContext(l.parentItem);o[u]&&o[u][h]&&m.subCache?(h=Math.min(h,Math.floor(o[u].size/e.pageSize)),p=[],c(o[u][h],o[u].size)):(n[u][h]=c,e.$connector.fetchPage((v,b)=>e.$connector.beforeParentRequest(v,b,l.parentItem.key),h,u))}else{if(h=Math.min(h,Math.floor(e.size/e.pageSize)),e.size===0){c([],0);return}o[C]&&o[C][h]?c(o[C][h]):(s[h]=c,I=N.debounce(I,Je.after(e._hasData?w:0),()=>{e.$connector.fetchPage((u,m)=>e.$server.setRequestedRange(u,m),h,C)}))}}),e.$connector.setSorterDirections=r(function(l){H=!0,setTimeout(r(()=>{try{const c=Array.from(e.querySelectorAll("vaadin-grid-sorter"));e._sorters.forEach(h=>{c.includes(h)||c.push(h)}),c.forEach(h=>{h.direction=null}),e.multiSortPriority!=="append"&&(l=l.reverse()),l.forEach(({column:h,direction:u})=>{c.forEach(m=>{m.getAttribute("path")===h&&(m.direction=u)})})}finally{H=!1}}))}),e._updateItem=r(function(l,c){ce.prototype._updateItem.call(e,l,c),l.hidden||Array.from(l.children).forEach(h=>{h._content&&h._content.__templateInstance&&h._content.__templateInstance.children&&Array.from(h._content.__templateInstance.children).forEach(u=>{u._attachRenderedComponentIfAble&&u._attachRenderedComponentIfAble(),u.children&&Array.from(u.children).forEach(m=>{m._attachRenderedComponentIfAble&&m._attachRenderedComponentIfAble()})})}),M===G[1]&&(l.removeAttribute("aria-selected"),Array.from(l.children).forEach(h=>h.removeAttribute("aria-selected")))});const $=r(function(l,c){if(l==null||e.$server.updateExpandedState==null)return;let h=e.getItemId(l);e.$server.updateExpandedState(h,c)});e.expandItem=r(function(l){$(l,!0),ce.prototype.expandItem.call(e,l)}),e.collapseItem=r(function(l){$(l,!1),ce.prototype.collapseItem.call(e,l)});const te=function(l){if(!l||!Array.isArray(l))throw"Attempted to call itemsUpdated with an invalid value: "+JSON.stringify(l);let c=Array.from(e.detailsOpenedItems),h=!1;for(let u=0;u<l.length;++u){const m=l[u];m&&(m.detailsOpened?e._getItemIndexInArray(m,c)<0&&c.push(m):e._getItemIndexInArray(m,c)>=0&&c.splice(e._getItemIndexInArray(m,c),1),E[m.key]&&(E[m.key]=m,m.selected=!0,h=!0))}e.detailsOpenedItems=c,h&&e.selectedItems.splice(0,e.selectedItems.length,...Object.values(E))},ze=function(l,c){let h;if((c||C)!==C){h=o[c][l];const u=S(c),m=t.getItemContext(u);if(m&&m.subCache){const v=n[c],b=v&&v[l];Ue(l,h,b,m.subCache)}}else h=o[C][l],Ue(l,h,s[l],t.rootCache);return h},Ue=function(l,c,h,u){if(!h){let m=l*e.pageSize,v=m+e.pageSize;if(c){if(u&&u.items)for(let b=m;b<v;b++)u.items[b]&&(u.items[b]=c[b-m])}else if(u&&u.items)for(let b=m;b<v;b++)delete u.items[b]}},qe=function(){Pe(),e.__updateVisibleRows()},Pe=function(){t.recalculateFlatSize(),e._flatSize=t.flatSize},ge=function(l){if(!l||!e.$||e.$.items.childElementCount===0)return;const c=l.map(u=>u.key),h=e._getRenderedRows().filter(u=>u._item&&c.includes(u._item.key)).map(u=>u.index);h.length>0&&e.__updateVisibleRows(h[0],h[h.length-1])};e.$connector.set=r(function(l,c,h){if(l%e.pageSize!=0)throw"Got new data to index "+l+" which is not aligned with the page size of "+e.pageSize;let u=h||C;const m=l/e.pageSize,v=Math.ceil(c.length/e.pageSize);for(let b=0;b<v;b++){let A=m+b,y=c.slice(b*e.pageSize,(b+1)*e.pageSize);o[u]||(o[u]={}),o[u][A]=y,e.$connector.doSelection(y.filter(D=>D.selected)),e.$connector.doDeselection(y.filter(D=>!D.selected&&E[D.key]));const R=ze(A,u);R&&(te(R),ge(R))}});const je=function(l){let c=l.parentUniqueKey||C;if(o[c]){for(let h in o[c])for(let u in o[c][h])if(e.getItemId(o[c][h][u])===e.getItemId(l))return{page:h,index:u,parentKey:c}}return null};e.$connector.updateHierarchicalData=r(function(l){let c=[];for(let u=0;u<l.length;u++){let m=je(l[u]);if(m){o[m.parentKey][m.page][m.index]=l[u];let v=m.parentKey+":"+m.page;c[v]||(c[v]={parentKey:m.parentKey,page:m.page})}}let h=Object.keys(c);for(let u=0;u<h.length;u++){let m=c[h[u]];const v=ze(m.page,m.parentKey);v&&(te(v),ge(v))}}),e.$connector.updateFlatData=r(function(l){for(let c=0;c<l.length;c++){let h=je(l[c]);if(h){o[h.parentKey][h.page][h.index]=l[c];const u=parseInt(h.page)*e.pageSize+parseInt(h.index),{rootCache:m}=t;m.items[u]&&(m.items[u]=l[c])}}te(l),ge(l)}),e.$connector.clearExpanded=r(function(){e.expandedItems=[],p=[],_=[]}),e.$connector.clear=r(function(l,c,h){let u=h||C;if(!o[u]||Object.keys(o[u]).length===0)return;if(l%e.pageSize!=0)throw"Got cleared data for index "+l+" which is not aligned with the page size of "+e.pageSize;let m=Math.floor(l/e.pageSize),v=Math.ceil(c/e.pageSize);for(let y=0;y<v;y++){let R=m+y,D=o[u][R];e.$connector.doDeselection(D.filter(J=>E[J.key])),D.forEach(J=>e.closeItemDetails(J)),delete o[u][R];const Z=ze(R,h);Z&&te(Z),ge(D)}let b=t.rootCache;if(h){const y=S(u);b=t.getItemContext(y).subCache}const A=l+v*e.pageSize;for(let y=l;y<A;y++)delete b.items[y],b.removeSubCache(y);Pe()}),e.$connector.reset=r(function(){e.size=0,be(o),be(t.rootCache.items),be(T),g&&g.cancel(),f&&f.cancel(),I&&I.cancel(),g=void 0,f=void 0,p=[],_=[],qe()});const be=l=>Object.keys(l).forEach(c=>delete l[c]);e.$connector.updateSize=l=>e.size=l,e.$connector.updateUniqueItemIdPath=l=>e.itemIdPath=l,e.$connector.expandItems=r(function(l){let c=Array.from(e.expandedItems);l.filter(h=>!e._isExpanded(h)).forEach(h=>c.push(h)),e.expandedItems=c}),e.$connector.collapseItems=r(function(l){let c=Array.from(e.expandedItems);l.forEach(h=>{let u=e._getItemIndexInArray(h,c);u>=0&&c.splice(u,1)}),e.expandedItems=c,l.forEach(h=>e.$connector.removeFromQueue(h))}),e.$connector.removeFromQueue=r(function(l){let c=e.getItemId(l);Object.values(n[c]||{}).forEach(h=>h([])),delete n[c],p=p.filter(h=>h.itemkey!==c),_=_.filter(h=>h.parentKey!==c)}),e.$connector.confirmParent=r(function(l,c,h){o[c]||(o[c]={});const u=o[c].size!==h;o[c].size=h,h===0&&(o[c][0]=[]);let m=Object.getOwnPropertyNames(n[c]||{});for(let v=0;v<m.length;v++){let b=m[v],A=T[c]||[0,0];const y=n[c][b];if(o[c]&&o[c][b]||b<A[0]||b>A[1]){delete n[c][b];let R=o[c][b]||new Array(h);y(R,h)}else y&&h===0&&(delete n[c][b],y([],h))}if(u&&m.length===0){const v=S(c),b=t.getItemContext(v);b&&b.subCache&&(b.subCache.size=h),Pe()}e.$server.confirmParentUpdate(l,c),e.loading||(e.__confirmParentUpdateDebouncer=N.debounce(e.__confirmParentUpdateDebouncer,Ze,()=>e.__updateVisibleRows()))}),e.$connector.confirm=r(function(l){let c=Object.getOwnPropertyNames(s);for(let h=0;h<c.length;h++){let u=c[h],m=T[C]||[0,0];const v=e.size?Math.ceil(e.size/e.pageSize)-1:0,b=Math.min(m[1],v),A=s[u];o[C]&&o[C][u]||u<m[0]||+u>b?(delete s[u],o[C][u]?A(o[C][u]):(A(new Array(e.pageSize)),e.requestContentUpdate())):A&&e.size===0&&(delete s[u],A([]))}Object.keys(s).length&&delete T[C],e.$server.confirmUpdate(l)}),e.$connector.ensureHierarchy=r(function(){for(let l in o)l!==C&&delete o[l];be(T),t.rootCache.removeSubCaches(),qe()}),e.$connector.setSelectionMode=r(function(l){if((typeof l=="string"||l instanceof String)&&G.indexOf(l)>=0)M=l,E={},e.$connector.updateMultiSelectable();else throw"Attempted to set an invalid selection mode"}),e.$connector.updateMultiSelectable=r(function(){e.$&&(M===G[0]?e.$.table.setAttribute("aria-multiselectable",!1):M===G[1]?e.$.table.removeAttribute("aria-multiselectable"):e.$.table.setAttribute("aria-multiselectable",!0))}),e._createPropertyObserver("isAttached",()=>e.$connector.updateMultiSelectable());const Ke=l=>c=>{l&&(l(c),l=null)};e.$connector.setHeaderRenderer=r(function(l,c){const{content:h,showSorter:u,sorterPath:m}=c;if(h===null){l.headerRenderer=null;return}l.headerRenderer=Ke(v=>{v.innerHTML="";let b=v;if(u){const A=document.createElement("vaadin-grid-sorter");A.setAttribute("path",m);const y=h instanceof Node?h.textContent:h;y&&A.setAttribute("aria-label",`Sort by ${y}`),v.appendChild(A),b=A}h instanceof Node?b.appendChild(h):b.textContent=h})}),e.__applySorters=()=>{const l=e._mapSorters(),c=JSON.stringify(e._previousSorters)!==JSON.stringify(l);e._previousSorters=l,ce.prototype.__applySorters.call(e),c&&!H&&e.$server.sortersChanged(l)},e.$connector.setFooterRenderer=r(function(l,c){const{content:h}=c;if(h===null){l.footerRenderer=null;return}l.footerRenderer=Ke(u=>{u.innerHTML="",h instanceof Node?u.appendChild(h):u.textContent=h})}),e.addEventListener("vaadin-context-menu-before-open",r(function(l){const{key:c,columnId:h}=l.detail;e.$server.updateContextMenuTargetItem(c,h)})),e.getContextMenuBeforeOpenDetail=r(function(l){const c=l.detail.sourceEvent||l,h=e.getEventContext(c),u=h.item&&h.item.key||"",m=h.column&&h.column.id||"";return{key:u,columnId:m}}),e.addEventListener("click",r(l=>Ye(l,"item-click"))),e.addEventListener("dblclick",r(l=>Ye(l,"item-double-click"))),e.addEventListener("column-resize",r(l=>{e._getColumnsInOrder().filter(h=>!h.hidden).forEach(h=>{h.dispatchEvent(new CustomEvent("column-drag-resize"))}),e.dispatchEvent(new CustomEvent("column-drag-resize",{detail:{resizedColumnKey:l.detail.resizedColumn._flowId}}))})),e.addEventListener("column-reorder",r(l=>{const c=e._columnTree.slice(0).pop().filter(h=>h._flowId).sort((h,u)=>h._order-u._order).map(h=>h._flowId);e.dispatchEvent(new CustomEvent("column-reorder-all-columns",{detail:{columns:c}}))})),e.addEventListener("cell-focus",r(l=>{const c=e.getEventContext(l);["header","body","footer"].indexOf(c.section)!==-1&&e.dispatchEvent(new CustomEvent("grid-cell-focus",{detail:{itemKey:c.item?c.item.key:null,internalColumnId:c.column?c.column._flowId:null,section:c.section}}))}));function Ye(l,c){if(l.defaultPrevented)return;const h=l.target;if(Ht(h)||h instanceof HTMLLabelElement)return;const u=e.getEventContext(l),m=u.section;u.item&&m!=="details"&&(l.itemKey=u.item.key,u.column&&(l.internalColumnId=u.column._flowId),e.dispatchEvent(new CustomEvent(c,{detail:l})))}e.cellClassNameGenerator=r(function(l,c){const h=c.item.style;if(h)return(h.row||"")+" "+(l&&h[l._flowId]||"")}),e.cellPartNameGenerator=r(function(l,c){const h=c.item.part;if(h)return(h.row||"")+" "+(l&&h[l._flowId]||"")}),e.dropFilter=r(l=>l.item&&!l.item.dropDisabled),e.dragFilter=r(l=>l.item&&!l.item.dragDisabled),e.addEventListener("grid-dragstart",r(l=>{e._isSelected(l.detail.draggedItems[0])?(e.__selectionDragData?Object.keys(e.__selectionDragData).forEach(c=>{l.detail.setDragData(c,e.__selectionDragData[c])}):(e.__dragDataTypes||[]).forEach(c=>{l.detail.setDragData(c,l.detail.draggedItems.map(h=>h.dragData[c]).join(`
`))}),e.__selectionDraggedItemsCount>1&&l.detail.setDraggedItemsCount(e.__selectionDraggedItemsCount)):(e.__dragDataTypes||[]).forEach(c=>{l.detail.setDragData(c,l.detail.draggedItems[0].dragData[c])})}))})(i)}})();/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const tr=r=>class extends Mt(r){static get properties(){return{_childColumns:{value(){return this._getChildColumns(this)}},flexGrow:{type:Number,readOnly:!0,sync:!0},width:{type:String,readOnly:!0},_visibleChildColumns:Array,_colSpan:Number,_rootColumns:Array}}static get observers(){return["_groupFrozenChanged(frozen, _rootColumns)","_groupFrozenToEndChanged(frozenToEnd, _rootColumns)","_groupHiddenChanged(hidden)","_colSpanChanged(_colSpan, _headerCell, _footerCell)","_groupOrderChanged(_order, _rootColumns)","_groupReorderStatusChanged(_reorderStatus, _rootColumns)","_groupResizableChanged(resizable, _rootColumns)"]}connectedCallback(){super.connectedCallback(),this._addNodeObserver(),this._updateFlexAndWidth()}disconnectedCallback(){super.disconnectedCallback(),this._observer&&this._observer.disconnect()}_columnPropChanged(i,e){i==="hidden"&&(this._preventHiddenSynchronization=!0,this._updateVisibleChildColumns(this._childColumns),this._preventHiddenSynchronization=!1),/flexGrow|width|hidden|_childColumns/u.test(i)&&this._updateFlexAndWidth(),i==="frozen"&&!this.frozen&&(this.frozen=e),i==="lastFrozen"&&!this._lastFrozen&&(this._lastFrozen=e),i==="frozenToEnd"&&!this.frozenToEnd&&(this.frozenToEnd=e),i==="firstFrozenToEnd"&&!this._firstFrozenToEnd&&(this._firstFrozenToEnd=e)}_groupOrderChanged(i,e){if(e){const t=e.slice(0);if(!i){t.forEach(a=>{a._order=0});return}const s=/(0+)$/u.exec(i).pop().length,n=~~(Math.log(e.length)/Math.LN10)+1,o=10**(s-n);t[0]&&t[0]._order&&t.sort((a,d)=>a._order-d._order),Dt(t,o,i)}}_groupReorderStatusChanged(i,e){i===void 0||e===void 0||e.forEach(t=>{t._reorderStatus=i})}_groupResizableChanged(i,e){i===void 0||e===void 0||e.forEach(t=>{t.resizable=i})}_updateVisibleChildColumns(i){this._visibleChildColumns=Array.prototype.filter.call(i,e=>!e.hidden),this._colSpan=this._visibleChildColumns.length,this._updateAutoHidden()}_updateFlexAndWidth(){if(this._visibleChildColumns){if(this._visibleChildColumns.length>0){const i=this._visibleChildColumns.reduce((e,t)=>(e+=` + ${(t.width||"0px").replace("calc","")}`,e),"").substring(3);this._setWidth(`calc(${i})`)}else this._setWidth("0px");this._setFlexGrow(Array.prototype.reduce.call(this._visibleChildColumns,(i,e)=>i+e.flexGrow,0))}}__scheduleAutoFreezeWarning(i,e){if(this._grid){const t=e.replace(/([A-Z])/gu,"-$1").toLowerCase(),s=i[0][e]||i[0].hasAttribute(t);i.every(o=>(o[e]||o.hasAttribute(t))===s)||(this._grid.__autoFreezeWarningDebouncer=x.debounce(this._grid.__autoFreezeWarningDebouncer,j,()=>{console.warn(`WARNING: Joining ${e} and non-${e} Grid columns inside the same column group! This will automatically freeze all the joined columns to avoid rendering issues. If this was intentional, consider marking each joined column explicitly as ${e}. Otherwise, exclude the ${e} columns from the joined group.`)}))}}_groupFrozenChanged(i,e){e===void 0||i===void 0||i!==!1&&(this.__scheduleAutoFreezeWarning(e,"frozen"),Array.from(e).forEach(t=>{t.frozen=i}))}_groupFrozenToEndChanged(i,e){e===void 0||i===void 0||i!==!1&&(this.__scheduleAutoFreezeWarning(e,"frozenToEnd"),Array.from(e).forEach(t=>{t.frozenToEnd=i}))}_groupHiddenChanged(i){(i||this.__groupHiddenInitialized)&&this._synchronizeHidden(),this.__groupHiddenInitialized=!0}_updateAutoHidden(){const i=this._autoHidden;this._autoHidden=(this._visibleChildColumns||[]).length===0,(i||this._autoHidden)&&(this.hidden=this._autoHidden)}_synchronizeHidden(){this._childColumns&&!this._preventHiddenSynchronization&&this._childColumns.forEach(i=>{i.hidden=this.hidden})}_colSpanChanged(i,e,t){e&&(e.setAttribute("colspan",i),this._grid&&this._grid._a11yUpdateCellColspan(e,i)),t&&(t.setAttribute("colspan",i),this._grid&&this._grid._a11yUpdateCellColspan(t,i))}_getChildColumns(i){return q.getColumns(i)}_addNodeObserver(){this._observer=new q(this,()=>{this._preventHiddenSynchronization=!0,this._rootColumns=this._getChildColumns(this),this._childColumns=this._rootColumns,this._updateVisibleChildColumns(this._childColumns),this._preventHiddenSynchronization=!1,this._grid&&this._grid._debounceUpdateColumnTree&&this._grid._debounceUpdateColumnTree()}),this._observer.flush()}_isColumnElement(i){return i.nodeType===Node.ELEMENT_NODE&&/\bcolumn\b/u.test(i.localName)}};/**
 * @license
 * Copyright (c) 2016 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class ir extends tr(L){static get is(){return"vaadin-grid-column-group"}}V(ir);const Ae=window;Ae.Vaadin=Ae.Vaadin||{};Ae.Vaadin.setLitRenderer=(r,i,e,t,s,n)=>{const o=Function(`
    "use strict";

    const [render, html, returnChannel] = arguments;

    return (root, model, itemKey) => {
      const { item, index } = model;
      ${s.map(d=>`
          const ${d} = (...args) => {
            if (itemKey !== undefined) {
              returnChannel('${d}', itemKey, args[0] instanceof Event ? [] : [...args]);
            }
          }`).join("")}

      render(html\`${e}\`, root)
    }
  `)(At,It,t),a=(d,_,f)=>{const{item:p}=f;d.__litRenderer!==a&&(d.innerHTML="",delete d._$litPart$,d.__litRenderer=a);const g={};for(const w in p)w.startsWith(n)&&(g[w.replace(n,"")]=p[w]);o(d,{...f,item:g},p.key)};a.__rendererId=n,r[i]=a};Ae.Vaadin.unsetLitRenderer=(r,i,e)=>{var t;((t=r[i])==null?void 0:t.__rendererId)===e&&(r[i]=void 0)};const Lr=Object.freeze(Object.defineProperty({__proto__:null},Symbol.toStringTag,{value:"Module"}));export{N as D,ds as R,os as V,Lr as c,ar as e,Ve as g,Rt as i,Ot as m,Ge as p};
