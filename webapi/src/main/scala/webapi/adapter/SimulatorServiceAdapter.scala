package webapi.adapter

import webapi.core.service.SimulatorService
import webapi.mapper.SimulationDescriptionMapper
import webapi.protocol.CreateSimulation

import scala.concurrent.{ExecutionContext, Future}

class SimulatorServiceAdapter(service: SimulatorService)(implicit executor: ExecutionContext) {

  def register(command: CreateSimulation): Future[(String, CreateSimulation)] = {
    val description = SimulationDescriptionMapper.fromCreateSimulation(command)
    for (receipt <- service.register(description)) yield {
      (receipt.id, SimulationDescriptionMapper.toCreateSimulation(receipt.description))
    }
  }

}
