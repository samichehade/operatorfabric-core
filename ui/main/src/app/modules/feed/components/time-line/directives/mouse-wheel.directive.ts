import {Directive, EventEmitter, HostListener, Output} from '@angular/core';

/**
 * Mousewheel directive
 * https://github.com/SodhanaLibrary/angular2-examples/blob/master/app/mouseWheelDirective/mousewheel.directive.ts
 * @export
 */
@Directive({ selector: '[appMouseWheel]' })
export class MouseWheelDirective {
  @Output()
  mouseWheelUp = new EventEmitter();
  @Output()
  mouseWheelDown = new EventEmitter();

  @HostListener('mousewheel', ['$event'])
  onMouseWheelChrome(event: any): void {
    this.mouseWheelFunc(event);
  }

  @HostListener('DOMMouseScroll', ['$event'])
  onMouseWheelFirefox(event: any): void {
    this.mouseWheelFunc(event);
  }

  @HostListener('onmousewheel', ['$event'])
  onMouseWheelIE(event: any): void {
    this.mouseWheelFunc(event);
  }

  /**
   * emit mouse wheel up or down event
   * @param event
   */
  mouseWheelFunc(event: any): void {
    if (window.event) {
      event = window.event;
    }

    let delta = 0;
    delta = Math.max(-1, Math.min(1, event.wheelDelta || -event.detail));
    if (delta > 0) {
      this.mouseWheelUp.emit(event);
    } else if (delta < 0) {
      this.mouseWheelDown.emit(event);
    }

    // for IE
    if (event) {
      event.returnValue = false;
    }

    // for Chrome and Firefox
    if (event.preventDefault) {
      event.preventDefault();
    }
  }
}
