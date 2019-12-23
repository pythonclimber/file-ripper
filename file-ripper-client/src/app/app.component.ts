import {Component, OnInit} from '@angular/core';
import {FileType} from "./models/file-type";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'file-ripper-client';
  fileTypes: Array<any>;
  fileType: String

  ngOnInit(): void {
    this.fileTypes = [
      {
        label: 'Select File Type',
        value: null
      },
      {
        label: 'Delimited',
        value: 'DELIMITED'
      },
      {
        label: 'FIXED',
        value: 'Fixed Width'
      },{
        label: 'Xml',
        value: 'XML'
      },
    ]
  }
}
