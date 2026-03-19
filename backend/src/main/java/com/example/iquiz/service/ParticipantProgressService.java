package com.example.iquiz.service;

import com.example.iquiz.dto.ProgressDTO;
import com.example.iquiz.entity.ParticipantProgress;
import com.example.iquiz.mapper.ParticipantProgressMapper;
import com.example.iquiz.repository.ParticipantProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantProgressService {
    @Autowired
    private final ParticipantProgressRepository participantProgressRepository;
    @Autowired
    private final ParticipantProgressMapper participantProgressMapper;

    public List<ProgressDTO> findIncompletedCoursesByParticipantId(UUID participantId) {
        if (participantId == null) {
            throw new IllegalArgumentException("Participant ID cannot be null");
        }

        List<ParticipantProgress> progressList = participantProgressRepository.findAllByUser_Id(participantId);
        return progressList.stream()
                .map(participantProgressMapper::toDto)
                .toList();
    }

    public List<ProgressDTO> findCompletedCoursesByParticipantId(UUID participantId) {
        if (participantId == null) {
            throw new IllegalArgumentException("Participant ID cannot be null");
        }

        List<ParticipantProgress> progressList = participantProgressRepository.findAllByUser_Id(participantId);
        return progressList.stream()
                .filter(participantProgress -> {
                    return participantProgress.getProgressPercent().intValue() >= 100;
                })
                .map(participantProgressMapper::toDto)
                .toList();
    }
}
