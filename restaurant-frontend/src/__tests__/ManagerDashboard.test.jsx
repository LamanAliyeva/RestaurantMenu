/**
 * @vitest-environment jsdom
 */

import React from 'react'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { describe, it, expect, vi } from 'vitest'

import api from '../api'
import ManagerDashboard from '../components/ManagerDashboard'

// Mock the API module
vi.mock('../api')

// Mock OrderCard as the default export of its module
vi.mock('../components/OrderCard', () => ({
  default: (props) => (
    <div data-testid="order-card">
      <span>Order #{props.order.id}</span>
      {props.actionLabel && (
        <button onClick={props.onAction}>{props.actionLabel}</button>
      )}
    </div>
  )
}))

describe('ManagerDashboard', () => {
  it('renders a list of orders and handles complete action', async () => {
    // Arrange: two orders, one served, one pending
    const orders = [
      { id: 1, status: 'served' },
      { id: 2, status: 'pending' }
    ]
    api.get.mockResolvedValue({ data: orders })
    api.put.mockResolvedValue({})

    // Act: render the dashboard
    render(<ManagerDashboard />)

    // Assert: both OrderCards appear
    await waitFor(() => {
      expect(screen.getAllByTestId('order-card')).toHaveLength(2)
    })

    // Only the served order has a "Mark as Completed" button
    const buttons = screen.getAllByRole('button', { name: /mark as completed/i })
    expect(buttons).toHaveLength(1)

    // Click that button
    fireEvent.click(buttons[0])

    // The correct API call was made
    expect(api.put).toHaveBeenCalledWith('/orders/1/status', { status: 'completed' })

    // After resolving, the served order should be removed from the DOM
    await waitFor(() => {
      expect(screen.queryByText('Order #1')).toBeNull()
      expect(screen.getByText('Order #2')).toBeDefined()

    })
  })

  it('shows an error message if fetching fails', async () => {
    // Arrange: API get throws
    api.get.mockRejectedValueOnce(new Error('fail'))

    // Act
    render(<ManagerDashboard />)

    // Assert: error text appears
    await waitFor(() => {
      expect(screen.getByText(/could not load orders/i)).toBeDefined()

    })
  })
})
