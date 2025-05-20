/**
 * @vitest-environment jsdom
 */

import React from 'react'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect, vi } from 'vitest'

import TrackOrder from '../pages/TrackOrder'
import api from '../api'

// Mock the API module
vi.mock('../api')

describe('TrackOrder', () => {
    const mockData = {
        data: {
            trackCode: '42',
            status: 'served',
            createdAt: '2025-05-20T10:00:00Z',
            estimatedReadyAt: '2025-05-20T10:15:00Z'
        }
    }

    it('shows loading then details on success', async () => {
        // Arrange: API resolves with mockData
        api.get.mockResolvedValueOnce(mockData)

        // Act: render the component
        render(<TrackOrder />)

        // Initially shows the loading message
        expect(screen.getByText(/checking your order status/i)).toBeDefined()

        // Wait for the API call to finish and display the details
        await waitFor(() => {
            expect(screen.getByText(/track your order/i)).toBeDefined()
        })

        // Order number appears
        expect(screen.getByText('42')).toBeDefined()

        // There are two "served" elements (badge + message),
        // so assert at least one is present
        const servedElements = screen.getAllByText(/served/i)
        expect(servedElements.length).toBeGreaterThanOrEqual(1)

        // Estimated ready time label appears
        expect(screen.getByText(/estimated ready time/i)).toBeDefined()
    })

    it('displays an error message on failure', async () => {
        // Arrange: API rejects
        api.get.mockRejectedValueOnce(new Error('not found'))

        // Act
        render(<TrackOrder />)

        // Assert: the error message is shown
        await waitFor(() => {
            expect(
                screen.getByText(/invalid or expired tracking code/i)
            ).toBeDefined()
        })
    })
})
