package me.siddheshkothadi.autofism3.database

import me.siddheshkothadi.autofism3.model.PendingUploadFish
import me.siddheshkothadi.autofism3.model.Predictions
import me.siddheshkothadi.autofism3.model.SoilType
import me.siddheshkothadi.autofism3.model.UploadHistoryFish

fun PendingUploadFishEntity.toPendingUploadFish(): PendingUploadFish {
    return PendingUploadFish(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        workId = workId,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg,
        name = name,
        moisture = moisture,
        ph = ph,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        soc = soc,
    )
}

fun PendingUploadFish.toPendingUploadFishEntity(): PendingUploadFishEntity {
    return PendingUploadFishEntity(
        imageUri = imageUri,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        workId = workId,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg,
        name = name,
        moisture = moisture,
        ph = ph,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        soc = soc,
    )
}

fun UploadHistoryFishEntity.toUploadHistoryFish(): UploadHistoryFish {
    return UploadHistoryFish(
        _id = _id,
        name = name,
        image_url = image_url,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg,
        moisture = moisture,
        predictions = Predictions(SoilType(confidence, soilType)),
        ph = ph,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        soc = soc,
    )
}

fun UploadHistoryFish.toUploadHistoryFishEntity(): UploadHistoryFishEntity {
    return UploadHistoryFishEntity(
        _id = _id,
        name = name,
        image_url = image_url,
        longitude = longitude,
        latitude = latitude,
        quantity = quantity,
        timestamp = timestamp,
        temp = temp,
        pressure = pressure,
        humidity = humidity,
        speed = speed,
        deg = deg,
        moisture = moisture,
        confidence = predictions?.soilType?.confidence,
        soilType = predictions?.soilType?.type,
        ph = ph,
        nitrogen = nitrogen,
        phosphorus = phosphorus,
        potassium = potassium,
        soc = soc,
    )
}