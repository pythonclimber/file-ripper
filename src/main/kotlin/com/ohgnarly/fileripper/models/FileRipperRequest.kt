package com.ohgnarly.fileripper.models

import org.springframework.web.multipart.MultipartFile

class FileRipperRequest {
    var fileDefinition: FileDefinition? = null
    var multipartFile: MultipartFile? = null
}