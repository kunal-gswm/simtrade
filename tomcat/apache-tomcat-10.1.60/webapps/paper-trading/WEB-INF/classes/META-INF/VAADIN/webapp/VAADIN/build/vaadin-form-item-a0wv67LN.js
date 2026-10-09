import{r as h,E as w,T as _,P as c,h as p,d as f,a as S,b as C,g as E}from"./chunk-8463e6da51a94694d59b00e3455214aa0d3a0a2d1374f94614beac546593180b-BS53ne6M.js";import{i as b}from"./indexhtml-CHKSUbna.js";import{R as x}from"./chunk-e11278c51e7a4866a3f7e5b4d37c1ba52678e971eaeb26237e3a4acbad002b33-DzQyTbjc.js";h("vaadin-form-layout",b`
    :host {
      --vaadin-form-layout-column-spacing: var(--lumo-space-l);
    }
  `,{moduleId:"lumo-form-layout"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class L extends x(w(_(c))){static get template(){return p`
      <style>
        :host {
          display: block;
          max-width: 100%;
          animation: 1ms vaadin-form-layout-appear;
          /* CSS API for host */
          --vaadin-form-item-label-width: 8em;
          --vaadin-form-item-label-spacing: 1em;
          --vaadin-form-item-row-spacing: 1em;
          --vaadin-form-layout-column-spacing: 2em; /* (default) */
          align-self: stretch;
        }

        @keyframes vaadin-form-layout-appear {
          to {
            opacity: 1 !important; /* stylelint-disable-line keyframe-declaration-no-important */
          }
        }

        :host([hidden]) {
          display: none !important;
        }

        #layout {
          display: flex;

          align-items: baseline; /* default \`stretch\` is not appropriate */

          flex-wrap: wrap; /* the items should wrap */
        }

        #layout ::slotted(*) {
          /* Items should neither grow nor shrink. */
          flex-grow: 0;
          flex-shrink: 0;

          /* Margins make spacing between the columns */
          margin-left: calc(0.5 * var(--vaadin-form-layout-column-spacing));
          margin-right: calc(0.5 * var(--vaadin-form-layout-column-spacing));
        }

        #layout ::slotted(br) {
          display: none;
        }
      </style>
      <div id="layout">
        <slot id="slot"></slot>
      </div>
    `}static get is(){return"vaadin-form-layout"}static get properties(){return{responsiveSteps:{type:Array,value(){return[{minWidth:0,columns:1,labelsPosition:"top"},{minWidth:"20em",columns:1},{minWidth:"40em",columns:2}]},observer:"_responsiveStepsChanged"},_columnCount:{type:Number},_labelsOnTop:{type:Boolean}}}static get observers(){return["_invokeUpdateLayout(_columnCount, _labelsOnTop)"]}ready(){this._styleElement=document.createElement("style"),this.appendChild(this._styleElement),this._styleElement.textContent=" ",super.ready(),this.addEventListener("animationend",this.__onAnimationEnd)}connectedCallback(){super.connectedCallback(),requestAnimationFrame(()=>this._selectResponsiveStep()),requestAnimationFrame(()=>this._updateLayout()),this._observeChildrenColspanChange()}disconnectedCallback(){super.disconnectedCallback(),this.__mutationObserver.disconnect(),this.__childObserver.disconnect()}_observeChildrenColspanChange(){const e={attributes:!0};this.__mutationObserver=new MutationObserver(i=>{i.forEach(t=>{t.type==="attributes"&&(t.attributeName==="colspan"||t.attributeName==="hidden")&&this._updateLayout()})}),[...this.children].forEach(i=>{this.__mutationObserver.observe(i,e)}),this.__childObserver=new MutationObserver(i=>{const t=[],s=[];i.forEach(o=>{t.push(...this._getObservableNodes(o.addedNodes)),s.push(...this._getObservableNodes(o.removedNodes))}),t.forEach(o=>{this.__mutationObserver.observe(o,e)}),(t.length>0||s.length>0)&&this._updateLayout()}),this.__childObserver.observe(this,{childList:!0})}_getObservableNodes(e){const i=["template","style","dom-repeat","dom-if"];return Array.from(e).filter(t=>t.nodeType===Node.ELEMENT_NODE&&i.indexOf(t.localName.toLowerCase())===-1)}_naturalNumberOrOne(e){return typeof e=="number"&&e>=1&&e<1/0?Math.floor(e):1}_isValidCSSLength(e){return e==="inherit"||e==="normal"?!1:(this._styleElement.firstChild.nodeValue=`#styleElement { word-spacing: ${e}; }`,this._styleElement.sheet?["",null].indexOf(this._styleElement.sheet.cssRules[0].style.getPropertyValue("word-spacing"))<0:!0)}_responsiveStepsChanged(e,i){try{if(!Array.isArray(e))throw new Error('Invalid "responsiveSteps" type, an Array is required.');if(e.length<1)throw new Error('Invalid empty "responsiveSteps" array, at least one item is required.');e.forEach(t=>{if(this._naturalNumberOrOne(t.columns)!==t.columns)throw new Error(`Invalid 'columns' value of ${t.columns}, a natural number is required.`);if(t.minWidth!==void 0&&!this._isValidCSSLength(t.minWidth))throw new Error(`Invalid 'minWidth' value of ${t.minWidth}, a valid CSS length required.`);if(t.labelsPosition!==void 0&&["aside","top"].indexOf(t.labelsPosition)===-1)throw new Error(`Invalid 'labelsPosition' value of ${t.labelsPosition}, 'aside' or 'top' string is required.`)})}catch(t){i&&i!==e?(console.warn(`${t.message} Using previously set 'responsiveSteps' instead.`),this.responsiveSteps=i):(console.warn(`${t.message} Using default 'responsiveSteps' instead.`),this.responsiveSteps=[{minWidth:0,columns:1,labelsPosition:"top"},{minWidth:"20em",columns:1},{minWidth:"40em",columns:2}])}this._selectResponsiveStep()}__onAnimationEnd(e){e.animationName.indexOf("vaadin-form-layout-appear")===0&&this._selectResponsiveStep()}_selectResponsiveStep(){let e;const i="background-position";this.responsiveSteps.forEach(t=>{this.$.layout.style.setProperty(i,t.minWidth),parseFloat(getComputedStyle(this.$.layout).getPropertyValue(i))<=this.offsetWidth&&(e=t)}),this.$.layout.style.removeProperty(i),e&&(this._columnCount=e.columns,this._labelsOnTop=e.labelsPosition==="top")}_invokeUpdateLayout(){this._updateLayout()}_updateLayout(){const e=getComputedStyle(this),i=e.getPropertyValue("--vaadin-form-layout-column-spacing"),t=e.direction,s=`margin-${t==="ltr"?"left":"right"}`,o=`margin-${t==="ltr"?"right":"left"}`,y=this.offsetWidth;let l=0;Array.from(this.children).filter(a=>a.localName==="br"||getComputedStyle(a).display!=="none").forEach((a,g,r)=>{if(a.localName==="br"){l=0;return}let n;n=this._naturalNumberOrOne(parseFloat(a.getAttribute("colspan"))),n=Math.min(n,this._columnCount);const d=n/this._columnCount;a.style.width=`calc(${d*99.9}% - ${1-d} * ${i})`,l+n>this._columnCount&&(l=0),l===0?a.style.setProperty(s,"0px"):a.style.removeProperty(s);const u=g+1,N=u<r.length&&r[u].localName==="br";if(l+n===this._columnCount)a.style.setProperty(o,"0px");else if(N){const m=(this._columnCount-l-n)/this._columnCount;a.style.setProperty(o,`calc(${m*y}px + ${m} * ${i})`)}else a.style.removeProperty(o);l=(l+n)%this._columnCount,a.localName==="vaadin-form-item"&&(this._labelsOnTop?a.getAttribute("label-position")!=="top"&&(a.__useLayoutLabelPosition=!0,a.setAttribute("label-position","top")):a.__useLayoutLabelPosition&&(delete a.__useLayoutLabelPosition,a.removeAttribute("label-position")))})}_onResize(){this._selectResponsiveStep()}}f(L);h("vaadin-form-item",b`
    :host {
      --vaadin-form-item-row-spacing: 0;
    }

    /* font-weight, margin-bottom, transition and line-height same as for part label in text-field */
    [part='label'] {
      color: var(--lumo-secondary-text-color);
      font-family: var(--lumo-font-family);
      font-size: var(--lumo-font-size-s);
      font-weight: 500;
      margin-top: var(--lumo-space-m);
      margin-left: calc(var(--lumo-border-radius-m) / 4);
      margin-bottom: var(--lumo-space-xs);
      transition: color 0.4s;
      line-height: 1.333;
    }

    [part='required-indicator']::after {
      content: var(--lumo-required-field-indicator, '\\2022');
      transition: opacity 0.2s;
      opacity: 0;
      color: var(--lumo-required-field-indicator-color, var(--lumo-primary-text-color));
      position: relative;
      width: 1em;
      text-align: center;
    }

    :host([required]) [part='required-indicator']::after {
      opacity: 1;
    }

    :host([invalid]) [part='required-indicator']::after {
      color: var(--lumo-required-field-indicator-color, var(--lumo-error-text-color));
    }
  `,{moduleId:"lumo-form-item"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class O extends _(c){static get template(){return p`
      <style>
        :host {
          display: inline-flex;
          flex-direction: row;
          align-items: baseline;
          margin: calc(0.5 * var(--vaadin-form-item-row-spacing, 1em)) 0;
        }

        :host([label-position='top']) {
          flex-direction: column;
          align-items: stretch;
        }

        :host([hidden]) {
          display: none !important;
        }

        #label {
          width: var(--vaadin-form-item-label-width, 8em);
          flex: 0 0 auto;
        }

        :host([label-position='top']) #label {
          width: auto;
        }

        #spacing {
          width: var(--vaadin-form-item-label-spacing, 1em);
          flex: 0 0 auto;
        }

        #content {
          flex: 1 1 auto;
        }

        #content ::slotted(.full-width) {
          box-sizing: border-box;
          width: 100%;
          min-width: 0;
        }
      </style>
      <div id="label" part="label" on-click="__onLabelClick">
        <slot name="label" id="labelSlot" on-slotchange="__onLabelSlotChange"></slot>
        <span part="required-indicator" aria-hidden="true"></span>
      </div>
      <div id="spacing"></div>
      <div id="content">
        <slot id="contentSlot" on-slotchange="__onContentSlotChange"></slot>
      </div>
    `}static get is(){return"vaadin-form-item"}constructor(){super(),this.__updateInvalidState=this.__updateInvalidState.bind(this),this.__fieldNodeObserver=new MutationObserver(()=>this.__updateRequiredState(this.__fieldNode.required)),this.__labelNode=null,this.__fieldNode=null}_getFieldAriaTarget(e){return e.ariaTarget||e}__linkLabelToField(e){S(this._getFieldAriaTarget(e),"aria-labelledby",this.__labelId)}__unlinkLabelFromField(e){C(this._getFieldAriaTarget(e),"aria-labelledby",this.__labelId)}__onLabelClick(){const e=this.__fieldNode;e&&(e.focus(),e.click())}__getValidateFunction(e){return e.validate||e.checkValidity}__onLabelSlotChange(){this.__labelNode&&(this.__labelNode=null,this.__fieldNode&&this.__unlinkLabelFromField(this.__fieldNode));const e=this.$.labelSlot.assignedElements()[0];e&&(this.__labelNode=e,this.__labelNode.id?this.__labelId=this.__labelNode.id:(this.__labelId=`label-${this.localName}-${E()}`,this.__labelNode.id=this.__labelId),this.__fieldNode&&this.__linkLabelToField(this.__fieldNode))}__onContentSlotChange(){this.__fieldNode&&(this.__unlinkLabelFromField(this.__fieldNode),this.__updateRequiredState(!1),this.__fieldNodeObserver.disconnect(),this.__fieldNode=null);const e=this.$.contentSlot.assignedElements();e.length>1&&console.warn(`WARNING: Since Vaadin 23, placing multiple fields directly to a <vaadin-form-item> is deprecated.
Please wrap fields with a <vaadin-custom-field> instead.`);const i=e.find(t=>!!this.__getValidateFunction(t));i&&(this.__fieldNode=i,this.__updateRequiredState(this.__fieldNode.required),this.__fieldNodeObserver.observe(this.__fieldNode,{attributes:!0,attributeFilter:["required"]}),this.__labelNode&&this.__linkLabelToField(this.__fieldNode))}__updateRequiredState(e){e?(this.setAttribute("required",""),this.__fieldNode.addEventListener("blur",this.__updateInvalidState),this.__fieldNode.addEventListener("change",this.__updateInvalidState)):(this.removeAttribute("invalid"),this.removeAttribute("required"),this.__fieldNode.removeEventListener("blur",this.__updateInvalidState),this.__fieldNode.removeEventListener("change",this.__updateInvalidState))}__updateInvalidState(){const e=this.__getValidateFunction(this.__fieldNode).call(this.__fieldNode);this.toggleAttribute("invalid",e===!1)}}f(O);
