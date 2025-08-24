package co.edu.uniquindio.FitZone.service.impl;

import co.edu.uniquindio.FitZone.dto.request.MembershipTypeRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipTypeResponse;
import co.edu.uniquindio.FitZone.exception.MembershipTypeNotFoundException;
import co.edu.uniquindio.FitZone.model.entity.MembershipType;
import co.edu.uniquindio.FitZone.model.enums.MembershipTypeName;
import co.edu.uniquindio.FitZone.repository.MembershipTypeRepository;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class MembershipTypeImpl implements IMembershipTypeService {

    private final MembershipTypeRepository membershipTypeRepository;

    public MembershipTypeImpl(MembershipTypeRepository membershipTypeRepository) {
        this.membershipTypeRepository = membershipTypeRepository;
    }

    @Override
    public MembershipTypeResponse createMembershipType(MembershipTypeRequest membershipTypeRequest) {

        if(membershipTypeRepository.existsByName(membershipTypeRequest.name())){
            throw new MembershipTypeNotFoundException("El tipo de membresía ya existe");
        }

        MembershipType membershipType = getMembershipType(membershipTypeRequest);

        MembershipType savedMembershipType = membershipTypeRepository.save(membershipType);

        return new MembershipTypeResponse(
                savedMembershipType.getIdMembershipType(),
                savedMembershipType.getName(),
                savedMembershipType.getDescription(),
                savedMembershipType.getMonthlyPrice(),
                savedMembershipType.getAccessToAllLocation(),
                savedMembershipType.getGroupClassesSessionsIncluded(),
                savedMembershipType.getPersonalTrainingIncluded(),
                savedMembershipType.getSpecializedClassesIncluded()
        );
    }


    @Override
    public MembershipTypeResponse updateMembershipType(Long id, MembershipTypeRequest request) {

        MembershipType membershipType = membershipTypeRepository.findById(id)
                .orElseThrow(() -> new MembershipTypeNotFoundException("Tipo de membresía no encontrado"));

       if(!membershipType.getName().equals(request.name()) && membershipTypeRepository.existsByName(request.name())){
           throw new MembershipTypeNotFoundException("El tipo de membresía ya existe");
       }

        membershipType.setName(request.name());
        membershipType.setDescription(request.description());
        membershipType.setMonthlyPrice(request.monthlyPrice());
        membershipType.setAccessToAllLocation(request.accessToAllLocation());
        membershipType.setGroupClassesSessionsIncluded(request.groupClassesSessionsIncluded());
        membershipType.setPersonalTrainingIncluded(request.personalTrainingIncluded());
        membershipType.setSpecializedClassesIncluded(request.specializedClassesIncluded());

        MembershipType updatedMembershipType = membershipTypeRepository.save(membershipType);

        return new MembershipTypeResponse(
                updatedMembershipType.getIdMembershipType(),
                updatedMembershipType.getName(),
                updatedMembershipType.getDescription(),
                updatedMembershipType.getMonthlyPrice(),
                updatedMembershipType.getAccessToAllLocation(),
                updatedMembershipType.getGroupClassesSessionsIncluded(),
                updatedMembershipType.getPersonalTrainingIncluded(),
                updatedMembershipType.getSpecializedClassesIncluded()
        );

    }

    @Override
    public MembershipTypeResponse getMembershipTypeById(Long id) {
        MembershipType membershipType = membershipTypeRepository.findById(id)
                .orElseThrow(() -> new MembershipTypeNotFoundException("Tipo de membresía no encontrado"));

        return new MembershipTypeResponse(
                membershipType.getIdMembershipType(),
                membershipType.getName(),
                membershipType.getDescription(),
                membershipType.getMonthlyPrice(),
                membershipType.getAccessToAllLocation(),
                membershipType.getGroupClassesSessionsIncluded(),
                membershipType.getPersonalTrainingIncluded(),
                membershipType.getSpecializedClassesIncluded()
        );
    }

    @Override
    public MembershipTypeResponse getMembershipTypeByName(MembershipTypeName name) {
        MembershipType membershipType = membershipTypeRepository.findByName(name)
                .orElseThrow(() -> new MembershipTypeNotFoundException("Tipo de membresía no encontrado"));

        return new MembershipTypeResponse(
                membershipType.getIdMembershipType(),
                membershipType.getName(),
                membershipType.getDescription(),
                membershipType.getMonthlyPrice(),
                membershipType.getAccessToAllLocation(),
                membershipType.getGroupClassesSessionsIncluded(),
                membershipType.getPersonalTrainingIncluded(),
                membershipType.getSpecializedClassesIncluded()
        );
    }

    @Override
    public List<MembershipTypeResponse> getMembershipTypes() {

        return StreamSupport
                .stream(membershipTypeRepository.findAll().spliterator(), false)
                .map(m -> new MembershipTypeResponse(
                        m.getIdMembershipType(),
                        m.getName(),
                        m.getDescription(),
                        m.getMonthlyPrice(),
                        m.getAccessToAllLocation(),
                        m.getGroupClassesSessionsIncluded(),
                        m.getPersonalTrainingIncluded(),
                        m.getSpecializedClassesIncluded()

                ))
                .toList();
    }

    /**
     * Método privado para mapear un MembershipTypeRequest a una entidad MembershipType
     * @param membershipTypeRequest DTO de solicitud que contiene los datos del tipo de membresía
     * @return Entidad MembershipType mapeada
     */
    private static MembershipType getMembershipType(MembershipTypeRequest membershipTypeRequest) {
        MembershipType membershipType = new MembershipType();
        membershipType.setName(membershipTypeRequest.name());
        membershipType.setDescription(membershipTypeRequest.description());
        membershipType.setMonthlyPrice(membershipTypeRequest.monthlyPrice());
        membershipType.setAccessToAllLocation(membershipTypeRequest.accessToAllLocation());
        membershipType.setGroupClassesSessionsIncluded(membershipTypeRequest.groupClassesSessionsIncluded());
        membershipType.setPersonalTrainingIncluded(membershipTypeRequest.personalTrainingIncluded());
        membershipType.setSpecializedClassesIncluded(membershipTypeRequest.specializedClassesIncluded());
        return membershipType;
    }

}
