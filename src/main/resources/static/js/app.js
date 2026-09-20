/**
 * ParkSmart - Modern Frontend Application Controller
 * Handles real-time grid polling, vehicle entry/exit, QR pass rendering, and analytics.
 */

document.addEventListener('DOMContentLoaded', () => {
    // State management
    const state = {
        activeTab: 'tab-grid',
        floors: [],
        slots: [],
        activeFloorFilter: 'all',
        autoRefreshInterval: null,
        refreshCountdown: 5,
        rates: []
    };

    // DOM Elements
    const elements = {
        // Navigation & Header
        tabButtons: document.querySelectorAll('.tab-btn'),
        tabPanes: document.querySelectorAll('.tab-pane'),
        liveClock: document.getElementById('liveClock'),
        navAvailableCount: document.getElementById('navAvailableCount'),
        navOccupiedCount: document.getElementById('navOccupiedCount'),
        navOccupancyRate: document.getElementById('navOccupancyRate'),

        // Grid
        parkingGridContainer: document.getElementById('parkingGridContainer'),
        floorButtonsContainer: document.getElementById('floorButtonsContainer'),
        autoRefreshToggle: document.getElementById('autoRefreshToggle'),
        refreshTimer: document.getElementById('refreshTimer'),
        btnManualRefresh: document.getElementById('btnManualRefresh'),

        // Vehicle Entry
        vehicleEntryForm: document.getElementById('vehicleEntryForm'),
        preferredFloorSelect: document.getElementById('preferredFloor'),
        preferredZoneSelect: document.getElementById('preferredZone'),
        passDisplayContainer: document.getElementById('passDisplayContainer'),
        btnSubmitEntry: document.getElementById('btnSubmitEntry'),

        // Vehicle Checkout
        checkoutPlateSearch: document.getElementById('checkoutPlateSearch'),
        btnSearchPlate: document.getElementById('btnSearchPlate'),
        checkoutPreviewSection: document.getElementById('checkoutPreviewSection'),
        checkoutEmptyState: document.getElementById('checkoutEmptyState'),
        btnConfirmCheckout: document.getElementById('btnConfirmCheckout'),
        receiptDisplayContainer: document.getElementById('receiptDisplayContainer'),

        // Analytics & Rates
        analyticsDateFilter: document.getElementById('analyticsDateFilter'),
        btnApplyDateFilter: document.getElementById('btnApplyDateFilter'),
        kpiOccupancyCount: document.getElementById('kpiOccupancyCount'),
        kpiOccupancyPct: document.getElementById('kpiOccupancyPct'),
        kpiAvailableCount: document.getElementById('kpiAvailableCount'),
        kpiRevenueToday: document.getElementById('kpiRevenueToday'),
        kpiSessionsToday: document.getElementById('kpiSessionsToday'),
        kpiTurnoverRate: document.getElementById('kpiTurnoverRate'),
        peakHoursChartContainer: document.getElementById('peakHoursChartContainer'),
        ratesListContainer: document.getElementById('ratesListContainer'),
        historyTableBody: document.getElementById('historyTableBody'),
        btnRefreshHistory: document.getElementById('btnRefreshHistory'),

        // Toasts & Modals
        toastContainer: document.getElementById('toastContainer'),
        printModal: document.getElementById('printModal'),
        modalTitle: document.getElementById('modalTitle'),
        modalBody: document.getElementById('modalBody'),
        modalCloseBtn: document.getElementById('modalCloseBtn'),
        modalDismissBtn: document.getElementById('modalDismissBtn')
    };

    // =========================================================================
    // 1. INITIALIZATION
    // =========================================================================
    function init() {
        startLiveClock();
        setupTabNavigation();
        setupRadioPills();
        setupEventListeners();

        // Default date filter to today (YYYY-MM-DD)
        const todayStr = new Date().toISOString().split('T')[0];
        if (elements.analyticsDateFilter) {
            elements.analyticsDateFilter.value = todayStr;
        }

        // Initial Data Fetch
        loadFloorsAndGrid();
        loadAnalytics();
        loadRates();
        loadParkingHistory();

        // Start Auto Refresh
        startAutoRefresh();
    }

    function startLiveClock() {
        const updateClock = () => {
            const now = new Date();
            elements.liveClock.textContent = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        };
        updateClock();
        setInterval(updateClock, 1000);
    }

    // =========================================================================
    // 2. TAB NAVIGATION
    // =========================================================================
    function setupTabNavigation() {
        elements.tabButtons.forEach(btn => {
            btn.addEventListener('click', () => {
                const targetTab = btn.getAttribute('data-tab');
                elements.tabButtons.forEach(b => b.classList.remove('active'));
                elements.tabPanes.forEach(p => p.classList.remove('active'));

                btn.classList.add('active');
                const targetPane = document.getElementById(targetTab);
                if (targetPane) targetPane.classList.add('active');
                state.activeTab = targetTab;

                // Tab specific refresh
                if (targetTab === 'tab-grid') loadFloorsAndGrid();
                if (targetTab === 'tab-analytics') {
                    loadAnalytics();
                    loadRates();
                    loadParkingHistory();
                }
            });
        });
    }

    function setupRadioPills() {
        document.querySelectorAll('.radio-pill').forEach(pill => {
            pill.addEventListener('click', () => {
                const group = pill.closest('.radio-pill-group');
                if (group) {
                    group.querySelectorAll('.radio-pill').forEach(p => p.classList.remove('active'));
                    pill.classList.add('active');
                    const radio = pill.querySelector('input[type="radio"]');
                    if (radio) radio.checked = true;
                }
            });
        });

        document.querySelectorAll('.pay-option').forEach(option => {
            option.addEventListener('click', () => {
                const parent = option.closest('.payment-options');
                if (parent) {
                    parent.querySelectorAll('.pay-option').forEach(o => o.classList.remove('active'));
                    option.classList.add('active');
                    const radio = option.querySelector('input[type="radio"]');
                    if (radio) radio.checked = true;
                }
            });
        });
    }

    // =========================================================================
    // 3. EVENT LISTENERS
    // =========================================================================
    function setupEventListeners() {
        // Grid controls
        elements.btnManualRefresh.addEventListener('click', () => {
            loadFloorsAndGrid();
            loadAnalytics();
            showToast('Grid refreshed', 'success');
        });

        elements.autoRefreshToggle.addEventListener('change', (e) => {
            if (e.target.checked) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });

        // Vehicle Entry Form Submit
        elements.vehicleEntryForm.addEventListener('submit', handleVehicleEntry);

        // Vehicle Checkout
        elements.btnSearchPlate.addEventListener('click', handleSearchActiveSession);
        elements.checkoutPlateSearch.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleSearchActiveSession();
            }
        });
        elements.btnConfirmCheckout.addEventListener('click', handleConfirmCheckout);

        // Analytics
        elements.btnApplyDateFilter.addEventListener('click', () => {
            loadAnalytics(elements.analyticsDateFilter.value);
        });
        elements.btnRefreshHistory.addEventListener('click', loadParkingHistory);

        // Modal close
        elements.modalCloseBtn.addEventListener('click', closeModal);
        elements.modalDismissBtn.addEventListener('click', closeModal);
        elements.printModal.addEventListener('click', (e) => {
            if (e.target === elements.printModal) closeModal();
        });

        // Dynamic zone filtering on floor preference change in entry form
        elements.preferredFloorSelect.addEventListener('change', updateZonePreferenceOptions);
    }

    // =========================================================================
    // 4. PARKING GRID LOGIC
    // =========================================================================
    async function loadFloorsAndGrid() {
        try {
            const [floorsRes, slotsRes] = await Promise.all([
                fetch('/api/floors'),
                fetch('/api/grid/slots')
            ]);

            const floorsData = await floorsRes.json();
            const slotsData = await slotsRes.json();

            if (floorsData.success && slotsData.success) {
                state.floors = floorsData.data || [];
                state.slots = slotsData.data || [];

                renderFloorFilterButtons();
                populateFloorPreferenceDropdown();
                renderParkingGrid();
                updateHeaderMetrics();
            }
        } catch (err) {
            console.error('Error fetching parking grid:', err);
            elements.parkingGridContainer.innerHTML = `
                <div class="empty-state">
                    <p style="color:var(--status-occupied);">⚠️ Could not connect to ParkSmart server. Ensure backend is running.</p>
                </div>
            `;
        }
    }

    function renderFloorFilterButtons() {
        const container = elements.floorButtonsContainer;
        let html = `<button class="floor-btn ${state.activeFloorFilter === 'all' ? 'active' : ''}" data-floor="all">All Floors</button>`;

        state.floors.forEach(floor => {
            const isActive = state.activeFloorFilter == floor.id;
            html += `<button class="floor-btn ${isActive ? 'active' : ''}" data-floor="${floor.id}">${escapeHtml(floor.floorName)}</button>`;
        });

        container.innerHTML = html;

        container.querySelectorAll('.floor-btn').forEach(btn => {
            btn.addEventListener('click', () => {
                container.querySelectorAll('.floor-btn').forEach(b => b.classList.remove('active'));
                btn.classList.add('active');
                state.activeFloorFilter = btn.getAttribute('data-floor');
                renderParkingGrid();
            });
        });
    }

    function renderParkingGrid() {
        const container = elements.parkingGridContainer;

        const filteredFloors = state.activeFloorFilter === 'all'
            ? state.floors
            : state.floors.filter(f => f.id == state.activeFloorFilter);

        if (filteredFloors.length === 0) {
            container.innerHTML = `<div class="empty-state"><p>No floors configured.</p></div>`;
            return;
        }

        let html = '';

        filteredFloors.forEach(floor => {
            // Find all slots belonging to this floor
            const floorSlots = state.slots.filter(s => s.zone && s.zone.floor && s.zone.floor.id === floor.id);
            const totalFloorSlots = floorSlots.length;
            const occupiedFloorSlots = floorSlots.filter(s => s.status === 'OCCUPIED').length;
            const availFloorSlots = totalFloorSlots - occupiedFloorSlots;

            html += `
                <div class="floor-card">
                    <div class="floor-header">
                        <div class="floor-name-badge">
                            <span class="floor-badge-tag">Level ${floor.floorNumber}</span>
                            <h3 class="floor-title-text">${escapeHtml(floor.floorName)}</h3>
                        </div>
                        <div class="floor-counts-text">
                            <span>Available: <strong style="color:var(--status-avail);">${availFloorSlots}</strong></span> |
                            <span>Occupied: <strong style="color:var(--status-occupied);">${occupiedFloorSlots}</strong> / ${totalFloorSlots}</span>
                        </div>
                    </div>
                    <div class="zones-container">
            `;

            // Render Zones for this floor
            if (floor.zones && floor.zones.length > 0) {
                floor.zones.forEach(zone => {
                    const zoneSlots = floorSlots.filter(s => s.zone && s.zone.id === zone.id);

                    html += `
                        <div class="zone-block">
                            <div class="zone-header">
                                <span>Zone ${escapeHtml(zone.zoneCode)}: ${escapeHtml(zone.zoneName)}</span>
                                <span>${zoneSlots.length} Bays</span>
                            </div>
                            <div class="slots-grid">
                    `;

                    zoneSlots.forEach(slot => {
                        const isAvail = slot.status === 'AVAILABLE';
                        const typeIcon = slot.vehicleType === 'FOUR_WHEELER' ? '🚗' : '🏍️';
                        const plateDisplay = slot.currentVehiclePlate ? escapeHtml(slot.currentVehiclePlate) : '&nbsp;';

                        html += `
                            <div class="slot-bay-card ${isAvail ? 'available' : 'occupied'}" 
                                 title="Bay: ${slot.slotCode} | Status: ${slot.status}">
                                <div class="bay-top-row">
                                    <span class="bay-code">${escapeHtml(slot.slotCode)}</span>
                                    <span class="bay-type-icon">${typeIcon}</span>
                                </div>
                                <div class="bay-plate-text">${plateDisplay}</div>
                                <div class="bay-status-badge">${slot.status}</div>
                            </div>
                        `;
                    });

                    html += `
                            </div>
                        </div>
                    `;
                });
            }

            html += `
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    function updateHeaderMetrics() {
        const total = state.slots.length;
        const occupied = state.slots.filter(s => s.status === 'OCCUPIED').length;
        const available = total - occupied;
        const pct = total > 0 ? Math.round((occupied / total) * 100) : 0;

        elements.navAvailableCount.textContent = available;
        elements.navOccupiedCount.textContent = occupied;
        elements.navOccupancyRate.textContent = `${pct}%`;
    }

    function startAutoRefresh() {
        stopAutoRefresh();
        state.refreshCountdown = 5;
        elements.refreshTimer.textContent = '5s';

        state.autoRefreshInterval = setInterval(() => {
            state.refreshCountdown--;
            if (state.refreshCountdown <= 0) {
                state.refreshCountdown = 5;
                loadFloorsAndGrid();
                if (state.activeTab === 'tab-analytics') loadAnalytics();
            }
            elements.refreshTimer.textContent = `${state.refreshCountdown}s`;
        }, 1000);
    }

    function stopAutoRefresh() {
        if (state.autoRefreshInterval) {
            clearInterval(state.autoRefreshInterval);
            state.autoRefreshInterval = null;
        }
        elements.refreshTimer.textContent = 'Paused';
    }

    // =========================================================================
    // 5. VEHICLE ENTRY & QR PASS WORKFLOW
    // =========================================================================
    function populateFloorPreferenceDropdown() {
        const select = elements.preferredFloorSelect;
        const currentVal = select.value;
        select.innerHTML = '<option value="">Auto-Assign (Nearest Available)</option>';

        state.floors.forEach(floor => {
            const opt = document.createElement('option');
            opt.value = floor.id;
            opt.textContent = `${floor.floorName} (Level ${floor.floorNumber})`;
            select.appendChild(opt);
        });

        select.value = currentVal;
    }

    function updateZonePreferenceOptions() {
        const floorId = elements.preferredFloorSelect.value;
        const zoneSelect = elements.preferredZoneSelect;
        zoneSelect.innerHTML = '<option value="">Auto-Assign (Any Zone)</option>';

        if (!floorId) return;

        const floor = state.floors.find(f => f.id == floorId);
        if (floor && floor.zones) {
            floor.zones.forEach(zone => {
                const opt = document.createElement('option');
                opt.value = zone.id;
                opt.textContent = `Zone ${zone.zoneCode} - ${zone.zoneName}`;
                zoneSelect.appendChild(opt);
            });
        }
    }

    async function handleVehicleEntry(e) {
        e.preventDefault();

        const form = elements.vehicleEntryForm;
        const licensePlate = form.licensePlate.value.trim().toUpperCase().replace(/\s+/g, '');
        const vehicleType = form.vehicleType.value;
        const ownerName = form.ownerName.value.trim();
        const mobileNumber = form.mobileNumber.value.trim();
        const preferredFloorId = form.preferredFloorId.value || null;
        const preferredZoneId = form.preferredZoneId.value || null;

        if (!licensePlate) {
            showToast('Please enter a license plate number', 'error');
            return;
        }

        const payload = {
            licensePlate,
            vehicleType,
            ownerName: ownerName || null,
            mobileNumber: mobileNumber || null,
            preferredFloorId: preferredFloorId ? parseInt(preferredFloorId) : null,
            preferredZoneId: preferredZoneId ? parseInt(preferredZoneId) : null
        };

        elements.btnSubmitEntry.disabled = true;

        try {
            const res = await fetch('/api/parking/entry', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();

            if (res.ok && data.success) {
                showToast(`Success! Allocated bay: ${data.data.slotCode}`, 'success');
                renderDigitalPass(data.data);
                form.reset();
                document.querySelectorAll('.radio-pill').forEach((p, idx) => {
                    p.classList.toggle('active', idx === 0);
                });
                loadFloorsAndGrid();
                loadAnalytics();
            } else {
                showToast(data.message || 'Vehicle check-in failed', 'error');
            }
        } catch (err) {
            console.error('Check-in error:', err);
            showToast('Server connection error. Please try again.', 'error');
        } finally {
            elements.btnSubmitEntry.disabled = false;
        }
    }

    function renderDigitalPass(pass) {
        const container = elements.passDisplayContainer;
        const passHtml = `
            <div class="digital-pass-card" id="activePassCard">
                <div class="pass-header">
                    <div class="pass-brand">Park<span>Smart</span> PASS</div>
                    <div class="pass-code-pill">${escapeHtml(pass.sessionCode)}</div>
                </div>

                <div class="pass-qr-box">
                    <img src="${pass.qrImageBase64}" alt="QR Parking Pass">
                </div>

                <div class="pass-details-table">
                    <div>
                        <div class="pass-item-label">Vehicle Plate</div>
                        <div class="pass-item-val" style="color:var(--accent-gold); font-family:'JetBrains Mono';">${escapeHtml(pass.licensePlate)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Vehicle Type</div>
                        <div class="pass-item-val">${pass.vehicleType === 'FOUR_WHEELER' ? '🚗 4-Wheeler' : '🏍️ 2-Wheeler'}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Allocated Bay</div>
                        <div class="pass-allocated-bay">${escapeHtml(pass.slotCode)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Location</div>
                        <div class="pass-item-val">${escapeHtml(pass.floorName)} (${escapeHtml(pass.zoneName)})</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Check-In Time</div>
                        <div class="pass-item-val">${escapeHtml(pass.entryTimeFormatted)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Owner Name</div>
                        <div class="pass-item-val">${pass.ownerName ? escapeHtml(pass.ownerName) : 'Visitor'}</div>
                    </div>
                </div>

                <div style="display:flex; gap:0.5rem;">
                    <button class="btn btn-primary btn-block" onclick="window.printPass()">
                        🖨️ Print / Save Pass
                    </button>
                </div>
            </div>
        `;

        container.className = '';
        container.innerHTML = passHtml;

        window.printPass = () => {
            openModal('Digital QR Parking Pass', document.getElementById('activePassCard').outerHTML);
        };
    }

    // =========================================================================
    // 6. VEHICLE CHECKOUT WORKFLOW
    // =========================================================================
    async function handleSearchActiveSession() {
        const plate = elements.checkoutPlateSearch.value.trim().toUpperCase().replace(/\s+/g, '');
        if (!plate) {
            showToast('Enter a license plate to search', 'error');
            return;
        }

        try {
            const res = await fetch(`/api/parking/active/${encodeURIComponent(plate)}`);
            const data = await res.json();

            if (res.ok && data.success) {
                renderCheckoutPreview(data.data);
            } else {
                elements.checkoutPreviewSection.style.display = 'none';
                elements.checkoutEmptyState.style.display = 'block';
                elements.checkoutEmptyState.innerHTML = `<p style="color:var(--status-occupied);">❌ ${escapeHtml(data.message || 'No active session found')}</p>`;
                showToast(data.message || 'No active session found', 'error');
            }
        } catch (err) {
            console.error('Search error:', err);
            showToast('Failed to contact server', 'error');
        }
    }

    function renderCheckoutPreview(preview) {
        elements.checkoutEmptyState.style.display = 'none';
        elements.checkoutPreviewSection.style.display = 'flex';

        document.getElementById('previewSlotCode').textContent = `Bay: ${preview.slotCode}`;
        document.getElementById('previewPlate').textContent = preview.licensePlate;
        document.getElementById('previewVehicleType').textContent = preview.vehicleType === 'FOUR_WHEELER' ? '🚗 4-Wheeler' : '🏍️ 2-Wheeler';
        document.getElementById('previewLocation').textContent = `${preview.floorName} - ${preview.zoneName}`;
        document.getElementById('previewEntryTime').textContent = preview.entryTimeFormatted;
        document.getElementById('previewExitTime').textContent = preview.checkoutTimeFormatted;
        document.getElementById('previewDuration').textContent = preview.durationFormatted;
        document.getElementById('previewExplanation').textContent = preview.calculationExplanation;
        document.getElementById('previewTotalFee').textContent = `₹${parseFloat(preview.totalFee).toFixed(2)}`;
    }

    async function handleConfirmCheckout() {
        const plate = elements.checkoutPlateSearch.value.trim().toUpperCase().replace(/\s+/g, '');
        const paymentRadio = document.querySelector('input[name="checkoutPaymentMethod"]:checked');
        const paymentMethod = paymentRadio ? paymentRadio.value : 'UPI';

        if (!plate) {
            showToast('License plate missing', 'error');
            return;
        }

        elements.btnConfirmCheckout.disabled = true;

        try {
            const res = await fetch('/api/parking/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ licensePlate: plate, paymentMethod })
            });

            const data = await res.json();

            if (res.ok && data.success) {
                showToast(`Payment received! Bay ${data.data.slotCode} is now FREE`, 'success');
                renderSettlementReceipt(data.data);

                // Reset search section
                elements.checkoutPreviewSection.style.display = 'none';
                elements.checkoutEmptyState.style.display = 'block';
                elements.checkoutPlateSearch.value = '';

                // Refresh grid & analytics
                loadFloorsAndGrid();
                loadAnalytics();
                loadParkingHistory();
            } else {
                showToast(data.message || 'Checkout failed', 'error');
            }
        } catch (err) {
            console.error('Checkout error:', err);
            showToast('Server error during checkout', 'error');
        } finally {
            elements.btnConfirmCheckout.disabled = false;
        }
    }

    function renderSettlementReceipt(receipt) {
        const container = elements.receiptDisplayContainer;
        const receiptHtml = `
            <div class="digital-pass-card" id="activeReceiptCard">
                <div class="pass-header">
                    <div class="pass-brand">Park<span>Smart</span> INVOICE</div>
                    <div class="pass-code-pill" style="color:var(--status-avail); background:rgba(16,185,129,0.15);">
                        PAID ✓
                    </div>
                </div>

                <div class="pass-details-table">
                    <div>
                        <div class="pass-item-label">Vehicle Plate</div>
                        <div class="pass-item-val" style="color:var(--accent-gold); font-family:'JetBrains Mono';">${escapeHtml(receipt.licensePlate)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Bay Released</div>
                        <div class="pass-allocated-bay">${escapeHtml(receipt.slotCode)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Duration</div>
                        <div class="pass-item-val">${escapeHtml(receipt.durationFormatted)}</div>
                    </div>
                    <div>
                        <div class="pass-item-label">Payment Method</div>
                        <div class="pass-item-val">${escapeHtml(receipt.paymentMethod)}</div>
                    </div>
                    <div style="grid-column: span 2;">
                        <div class="pass-item-label">Transaction Reference</div>
                        <div class="pass-item-val" style="font-family:'JetBrains Mono'; font-size:0.75rem;">${escapeHtml(receipt.transactionRef)}</div>
                    </div>
                </div>

                <div class="total-fee-row" style="margin-top:0.5rem;">
                    <span class="total-label">Total Amount Settled:</span>
                    <span class="total-amount">₹${parseFloat(receipt.totalFee).toFixed(2)}</span>
                </div>

                <div style="display:flex; gap:0.5rem; margin-top:0.75rem;">
                    <button class="btn btn-primary btn-block" onclick="window.printReceipt()">
                        🖨️ Print Tax Receipt
                    </button>
                </div>
            </div>
        `;

        container.className = '';
        container.innerHTML = receiptHtml;

        window.printReceipt = () => {
            openModal('Official Parking Tax Receipt', document.getElementById('activeReceiptCard').outerHTML);
        };
    }

    // =========================================================================
    // 7. ANALYTICS & DYNAMIC PRICING
    // =========================================================================
    async function loadAnalytics(dateStr = '') {
        try {
            const url = dateStr ? `/api/analytics/summary?date=${dateStr}` : '/api/analytics/summary';
            const res = await fetch(url);
            const data = await res.json();

            if (res.ok && data.success) {
                const s = data.data;
                elements.kpiOccupancyCount.textContent = `${s.occupiedSlots} / ${s.totalCapacity}`;
                elements.kpiOccupancyPct.textContent = `${s.occupancyPercentage}% capacity utilized`;
                elements.kpiAvailableCount.textContent = s.availableSlots;
                elements.kpiRevenueToday.textContent = `₹${parseFloat(s.todayRevenue).toFixed(2)}`;
                elements.kpiSessionsToday.textContent = `${s.totalSessionsToday} sessions recorded`;
                elements.kpiTurnoverRate.textContent = `${s.vehicleTurnoverRate}x`;

                renderPeakHoursChart(s.peakHours || []);
            }
        } catch (err) {
            console.error('Analytics error:', err);
        }
    }

    function renderPeakHoursChart(peakHours) {
        const container = elements.peakHoursChartContainer;
        if (!peakHours || peakHours.length === 0) {
            container.innerHTML = '<p class="text-muted" style="margin:auto;">No traffic data recorded today</p>';
            return;
        }

        const maxCount = Math.max(...peakHours.map(p => p.vehicleCount), 1);

        let html = '';
        peakHours.forEach(item => {
            const heightPct = Math.max(8, Math.round((item.vehicleCount / maxCount) * 100));
            html += `
                <div class="bar-col" title="${item.hourLabel}: ${item.vehicleCount} vehicles">
                    <div class="bar-fill-track">
                        <div class="bar-fill" style="height:${heightPct}%;">
                            ${item.vehicleCount > 0 ? `<span class="bar-val-badge">${item.vehicleCount}</span>` : ''}
                        </div>
                    </div>
                    <span class="bar-label">${escapeHtml(item.hourLabel)}</span>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    async function loadRates() {
        try {
            const res = await fetch('/api/rates');
            const data = await res.json();

            if (res.ok && data.success) {
                state.rates = data.data || [];
                renderRatesList();
            }
        } catch (err) {
            console.error('Rates fetch error:', err);
        }
    }

    function renderRatesList() {
        const container = elements.ratesListContainer;
        if (state.rates.length === 0) {
            container.innerHTML = '<p class="text-muted">No pricing rules defined.</p>';
            return;
        }

        let html = '';
        state.rates.forEach(rate => {
            const typeLabel = rate.vehicleType === 'FOUR_WHEELER' ? '🚗 4-Wheeler Rate' : '🏍️ 2-Wheeler Rate';

            html += `
                <div class="rate-item-box" data-id="${rate.id}">
                    <div class="rate-header-row">
                        <span class="rate-type-title">${typeLabel}</span>
                        <button class="btn btn-secondary btn-sm" onclick="window.saveRate(${rate.id})">Update Tariff</button>
                    </div>

                    <div class="rate-fields-row">
                        <div class="form-group">
                            <label>Base Fee (₹)</label>
                            <input type="number" id="baseRate_${rate.id}" value="${rate.baseRate}" step="5" min="0">
                        </div>
                        <div class="form-group">
                            <label>Hourly Fee (₹/hr)</label>
                            <input type="number" id="hourlyRate_${rate.id}" value="${rate.hourlyRate}" step="5" min="5">
                        </div>
                        <div class="form-group">
                            <label>Base Hours</label>
                            <input type="number" id="baseHours_${rate.id}" value="${rate.baseHours}" min="1">
                        </div>
                        <div class="form-group">
                            <label>Grace Period (Mins)</label>
                            <input type="number" id="gracePeriod_${rate.id}" value="${rate.gracePeriodMinutes}" min="0">
                        </div>
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;

        window.saveRate = async (rateId) => {
            const baseRate = parseFloat(document.getElementById(`baseRate_${rateId}`).value);
            const hourlyRate = parseFloat(document.getElementById(`hourlyRate_${rateId}`).value);
            const baseHours = parseInt(document.getElementById(`baseHours_${rateId}`).value);
            const gracePeriodMinutes = parseInt(document.getElementById(`gracePeriod_${rateId}`).value);

            try {
                const res = await fetch(`/api/rates/${rateId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ baseRate, hourlyRate, baseHours, gracePeriodMinutes })
                });

                const data = await res.json();
                if (res.ok && data.success) {
                    showToast('Pricing tariff updated in database', 'success');
                    loadRates();
                } else {
                    showToast(data.message || 'Update failed', 'error');
                }
            } catch (err) {
                showToast('Failed to update tariff', 'error');
            }
        };
    }

    async function loadParkingHistory() {
        try {
            const res = await fetch('/api/parking/history?page=0&size=10');
            const data = await res.json();

            if (res.ok && data.success && data.data && data.data.content) {
                renderHistoryTable(data.data.content);
            }
        } catch (err) {
            console.error('History error:', err);
        }
    }

    function renderHistoryTable(sessions) {
        const tbody = elements.historyTableBody;
        if (sessions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center">No parking sessions recorded yet.</td></tr>';
            return;
        }

        let html = '';
        sessions.forEach(s => {
            const isCompleted = s.status === 'COMPLETED';
            const statusClass = isCompleted ? 'status-completed' : 'status-active';
            const exitTimeDisplay = s.exitTime ? formatDate(s.exitTime) : '--';
            const feeDisplay = s.totalFee ? `₹${parseFloat(s.totalFee).toFixed(2)}` : '--';
            const durationDisplay = s.durationMinutes != null ? `${s.durationMinutes}m` : 'Active';

            html += `
                <tr>
                    <td style="font-family:'JetBrains Mono'; font-size:0.75rem;">${escapeHtml(s.sessionCode)}</td>
                    <td style="font-family:'JetBrains Mono'; font-weight:700; color:var(--accent-gold);">${escapeHtml(s.vehicle.licensePlate)}</td>
                    <td>${s.vehicle.vehicleType === 'FOUR_WHEELER' ? '🚗 4-Wheeler' : '🏍️ 2-Wheeler'}</td>
                    <td style="font-family:'JetBrains Mono'; font-weight:700;">${escapeHtml(s.slot.slotCode)}</td>
                    <td>${formatDate(s.entryTime)}</td>
                    <td>${exitTimeDisplay}</td>
                    <td>${durationDisplay}</td>
                    <td><strong>${feeDisplay}</strong></td>
                    <td><span class="status-tag ${statusClass}">${s.status}</span></td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    }

    // =========================================================================
    // 8. HELPERS & UTILITIES
    // =========================================================================
    function showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerHTML = `
            <span>${type === 'success' ? '✓' : '⚠️'}</span>
            <span>${escapeHtml(message)}</span>
        `;
        elements.toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    function openModal(title, bodyHtml) {
        elements.modalTitle.textContent = title;
        elements.modalBody.innerHTML = bodyHtml;
        elements.printModal.style.display = 'flex';
    }

    function closeModal() {
        elements.printModal.style.display = 'none';
    }

    function escapeHtml(str) {
        if (!str) return '';
        return String(str)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
    }

    function formatDate(dateInput) {
        if (!dateInput) return '';
        const d = new Date(dateInput);
        if (isNaN(d)) return dateInput;
        return d.toLocaleDateString([], { month: 'short', day: 'numeric' }) + ' ' +
               d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

    // Run app
    init();
});
